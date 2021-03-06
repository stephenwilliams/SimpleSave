/*
 * This file is part of SimpleSave
 *
 * SimpleSave is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleSave is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.UnknownTableException;
import com.alta189.simplesave.h2.H2Util;
import com.alta189.simplesave.internal.FieldRegistration;
import com.alta189.simplesave.internal.IdRegistration;
import com.alta189.simplesave.internal.PreparedStatementUtils;
import com.alta189.simplesave.internal.ResultSetUtils;
import com.alta189.simplesave.internal.TableRegistration;
import com.alta189.simplesave.internal.TableUtils;
import com.alta189.simplesave.query.Comparator;
import com.alta189.simplesave.query.Query;
import com.alta189.simplesave.query.QueryResult;
import com.alta189.simplesave.query.SelectQuery;
import com.alta189.simplesave.query.WhereEntry;
import com.alta189.simplesave.query.OrderQuery.Order;
import com.alta189.simplesave.query.OrderQuery.OrderPair;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class MySQLDatabase extends Database {
	private static final String driver = "mysql";
	private final String connUrl;
	private final String user;
	private final String pass;
	private Connection conn;

	static {
		DatabaseFactory.registerDatabase(MySQLDatabase.class);
	}

	public MySQLDatabase(Configuration config) {
		String user = config.getProperty(MySQLConstants.User);
		if (user == null || user.isEmpty()) {
			throw new IllegalArgumentException("Username is null or empty!");
		}

		String pass = config.getProperty(MySQLConstants.Password);
		if (pass == null) { // Password can be empty
			throw new IllegalArgumentException("Password is null!");
		}

		String host = config.getProperty(MySQLConstants.Host);
		if (host == null || host.isEmpty()) {
			throw new IllegalArgumentException("Host is null or empty!");
		}

		String port = config.getProperty(MySQLConstants.Port);
		if (port == null || port.isEmpty()) {
			throw new IllegalArgumentException("Port is null or empty!");
		}

		String database = config.getProperty(MySQLConstants.Database);
		if (database == null || database.isEmpty()) {
			throw new IllegalArgumentException("Database is null or empty!");
		}

		StringBuilder connUrl = new StringBuilder();
		connUrl.append("jdbc:mysql://");
		connUrl.append(host);
		connUrl.append(":");
		connUrl.append(port);
		connUrl.append("/");
		connUrl.append(database);
		connUrl.append("?useUnicode=true&characterEncoding=utf8");

		this.connUrl = connUrl.toString();
		this.user = user;
		this.pass = pass;
	}

	public MySQLDatabase(String connUrl, String user, String pass) {
		this.connUrl = connUrl;
		this.user = user;
		this.pass = pass;
	}

	public static String getDriver() {
		return driver;
	}

	@Override
	public void connect() throws ConnectionException {
		if (!isConnected()) {
			super.connect();

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				throw new ConnectionException("Could not find the MySQL JDBC Driver!", e);
			}

			try {
				conn = DriverManager.getConnection(connUrl, user, pass);
				createTables();
				if (checkTableOnRegistration()) {
					for (TableRegistration t : getTableRegistrations()){
						checkTableStructure(t);
					}
				}
				
			} catch (SQLException e) {
				throw new ConnectionException(e);
			}
		}
	}

	@Override
	public void close() throws ConnectionException {
		if (isConnected()) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new ConnectionException(e);
			}
			super.close();
		}
	}

	@Override
	public boolean isConnected() {
		try {
			return conn != null && !conn.isClosed() && conn.isValid(5000);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public <T> QueryResult<T> execute(Query<T> query) {
		if (!isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			switch (query.getType()) {
			case SELECT:
				SelectQuery selectQuery = (SelectQuery) query;
				TableRegistration table = getTableRegistration(selectQuery.getTableClass());
				PreparedStatement statement = null;
				StringBuilder queryBuilder = new StringBuilder();
				queryBuilder.append("SELECT * from ")
							.append(table.getName())
							.append(" ");
				if (!selectQuery.where().getEntries().isEmpty()) {
					queryBuilder.append("WHERE ");
					int count = 0;
					for (Object o : selectQuery.where().getEntries()) {
						count++;
						if (!(o instanceof WhereEntry)) {
							throw new InternalError("Something has gone very wrong!");
						}

						WhereEntry entry = (WhereEntry) o;
						if (entry.getPrefix() != null && !entry.getPrefix().isEmpty()) {
							queryBuilder.append(entry.getPrefix());
						}
						queryBuilder.append(entry.getField());
						switch (entry.getComparator()) {
							case EQUAL:
								queryBuilder.append("=? ");
								break;
							case NOT_EQUAL:
								queryBuilder.append("<>? ");
								break;
							case GREATER_THAN:
								queryBuilder.append(">? ");
								break;
							case LESS_THAN:
								queryBuilder.append("<? ");
								break;
							case GREATER_THAN_OR_EQUAL:
								queryBuilder.append(">=? ");
								break;
							case LESS_THAN_OR_EQUAL:
								queryBuilder.append("<=? ");
								break;
							case CONTAINS:
								queryBuilder.append(" LIKE ? ");
								break;
						}
						if (entry.getSuffix() != null && !entry.getSuffix().isEmpty()) {
							queryBuilder.append(entry.getSuffix());
						}
						if (count != selectQuery.where().getEntries().size()) {
							queryBuilder.append(entry.getOperator().name())
										.append(" ");
						}
					}
					if (!selectQuery.order().getPairs().isEmpty()){
						queryBuilder.append("ORDER BY ");
						int track = 0;
						for (Object pair : selectQuery.order().getPairs()){
							track++;
							if (!(pair instanceof OrderPair))
								throw new InternalError("Internal Error: Uncastable Object to OrderPair!");
							OrderPair order = (OrderPair)pair;
							queryBuilder.append(order.column).append(" ").append(order.order.name());
							if (track == selectQuery.order().getPairs().size())
								queryBuilder.append(" ");
							else
								queryBuilder.append(", ");
						}
					}
					if (selectQuery.limit().getLimit()!=null){
						queryBuilder.append("LIMIT ");
						if (selectQuery.limit().getStartFrom()!=null)
							queryBuilder.append(selectQuery.limit().getStartFrom()).append(", ");
						queryBuilder.append(selectQuery.limit().getLimit()).append(" ");
					}
					statement = conn.prepareStatement(queryBuilder.toString());
					count = 0;
					for (Object o : selectQuery.where().getEntries()) {
						count++;
						if (!(o instanceof WhereEntry)) {
							throw new InternalError("Something has gone very wrong!");
						}

						WhereEntry entry = (WhereEntry) o;
						if (entry.getComparator() == Comparator.CONTAINS) {
							statement.setString(count, "%" + entry.getComparison().getValue().toString() + "%");
						} else {
							PreparedStatementUtils.setObject(statement, count, entry.getComparison().getValue());
						}
					}
				}
				if (statement == null) {
					if (!selectQuery.order().getPairs().isEmpty()){
						queryBuilder.append("ORDER BY ");
						int track = 0;
						for (Object pair : selectQuery.order().getPairs()){
							track++;
							if (!(pair instanceof OrderPair))
								throw new InternalError("Internal Error: Uncastable Object to OrderPair!");
							OrderPair order = (OrderPair)pair;
							queryBuilder.append(order.column).append(" ").append(order.order.name());
							if (track == selectQuery.order().getPairs().size())
								queryBuilder.append(" ");
							else
								queryBuilder.append(", ");
						}
					}
					if (selectQuery.limit().getLimit()!=null){
						queryBuilder.append("LIMIT ");
						if (selectQuery.limit().getStartFrom()!=null)
							queryBuilder.append(selectQuery.limit().getStartFrom()).append(", ");
						queryBuilder.append(selectQuery.limit().getLimit()).append(" ");
					}
					statement = conn.prepareStatement(queryBuilder.toString());
				}
				ResultSet set = statement.executeQuery();
				QueryResult<T> result = new QueryResult<T>(ResultSetUtils.buildResultList(table, (Class<T>) table.getTableClass(), set));
				for (Object object : result.find()) {
					table.executePostInitialize(object);
				}
				set.close();
				return result;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public void save(Class<?> tableClass, Object o) {
		if (!isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}
		if (!tableClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("The provided table class and save objects classes were not compatible.");
		}

		TableRegistration table = getTableRegistration(tableClass);

		if (table == null) {
			throw new UnknownTableException("The table class '" + tableClass.getCanonicalName() + "' is not registered!");
		}

		StringBuilder query = new StringBuilder();
		long id = TableUtils.getIdValue(table, o);
		if (id == 0) {
			query.append("INSERT INTO ")
				 .append(table.getName())
				 .append(" (");
			StringBuilder valuesBuilder = new StringBuilder();
			valuesBuilder.append("VALUES ( ");
			int count = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				count++;
				query.append(fieldRegistration.getName());
				valuesBuilder.append("?");
				if (count == table.getFields().size()) {
					query.append(") ");
					valuesBuilder.append(")");
				} else {
					query.append(", ");
					valuesBuilder.append(", ");
				}
			}
			query.append(valuesBuilder.toString());
		} else {
			query.append("UPDATE ")
				 .append(table.getName())
				 .append(" SET ");
			int count = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				count++;
				query.append(fieldRegistration.getName())
					 .append("=?");
				if (count != table.getFields().size()) {
					query.append(", ");
				}
			}
			query.append(" WHERE ")
				 .append(table.getId().getName())
				 .append("=?");
		}

		try {
			PreparedStatement statement;
			if (id == 0) {
				statement = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = conn.prepareStatement(query.toString());
			}
			int i = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				i++;
				if (fieldRegistration.isSerializable()) {
					PreparedStatementUtils.setObject(statement, i, o);
				} else {
					if (TableUtils.isValueNull(fieldRegistration, o)) {
						PreparedStatementUtils.setObject(statement, i, null);
					} else if (fieldRegistration.getType().equals(int.class) || fieldRegistration.getType().equals(Integer.class)) {
						PreparedStatementUtils.setObject(statement, i, (Integer) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(long.class) || fieldRegistration.getType().equals(Long.class)) {
						PreparedStatementUtils.setObject(statement, i, (Long) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(double.class) || fieldRegistration.getType().equals(Double.class)) {
						PreparedStatementUtils.setObject(statement, i, (Double) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(String.class)) {
						PreparedStatementUtils.setObject(statement, i, (String) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(boolean.class) || fieldRegistration.getType().equals(Boolean.class)) {
						boolean value = (Boolean) TableUtils.getValue(fieldRegistration, o);
						if (value) {
							PreparedStatementUtils.setObject(statement, i, 1);
						} else {
							PreparedStatementUtils.setObject(statement, i, 0);
						}
					} else if (fieldRegistration.getType().equals(short.class) || fieldRegistration.getType().equals(Short.class)) {
						PreparedStatementUtils.setObject(statement, i, (Short) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(float.class) || fieldRegistration.getType().equals(Float.class)) {
						PreparedStatementUtils.setObject(statement, i, (Float) TableUtils.getValue(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(byte.class) || fieldRegistration.getType().equals(Byte.class)) {
						PreparedStatementUtils.setObject(statement, i, (Byte) TableUtils.getValue(fieldRegistration, o));
					} else {
						PreparedStatementUtils.setObject(statement, i, (Object) TableUtils.getValue(fieldRegistration, o));
					}
				}
			}

			if (id != 0) {
				i++;
				IdRegistration idRegistration = table.getId();
				if (idRegistration.getType().equals(Integer.class) || idRegistration.getType().equals(int.class)) {
					PreparedStatementUtils.setObject(statement, i, (Integer) TableUtils.getValue(idRegistration, o));
				} else if (idRegistration.getType().equals(Long.class) || idRegistration.getType().equals(long.class)) {
					PreparedStatementUtils.setObject(statement, i, (Long) TableUtils.getValue(idRegistration, o));
				}
			}

			statement.executeUpdate();
			if (id == 0) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet != null && resultSet.next()) {
					try {
						Field field = table.getId().getField();
						field.setAccessible(true);
						if (table.getId().getType().equals(int.class)) {
							field.setInt(o, resultSet.getInt(1));
						} else if (table.getId().getType().equals(Integer.class)) {
							field.set(o, resultSet.getObject(1));
						} else if (table.getId().getType().equals(long.class)) {
							field.setLong(o, resultSet.getLong(1));
						} else if (table.getId().getType().equals(Long.class)) {
							field.set(o, resultSet.getObject(1));
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			table.executePostInitialize(o);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(Class<?> tableClass, Object o) {
		if (!isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}
		if (!tableClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("The provided table class and save objects classes were not compatible.");
		}

		TableRegistration table = getTableRegistration(tableClass);

		if (table == null) {
			throw new UnknownTableException("The table class '" + tableClass.getCanonicalName() + "' is not registered!");
		}

		StringBuilder query = new StringBuilder();
		long id = TableUtils.getIdValue(table, o);
		if (id == 0)
			throw new IllegalArgumentException("Object was never inserted into database!");
		query.append("DELETE FROM ")
			 .append(table.getName())
			 .append(" WHERE ")
			 .append(table.getId().getName())
			 .append("=?");

		try {
			PreparedStatement statement = conn.prepareStatement(query.toString());

			IdRegistration idRegistration = table.getId();
			if (idRegistration.getType().equals(Integer.class) || idRegistration.getType().equals(int.class)) {
				PreparedStatementUtils.setObject(statement, 1, (Integer) TableUtils.getValue(idRegistration, o));
			} else if (idRegistration.getType().equals(Long.class) || idRegistration.getType().equals(long.class)) {
				PreparedStatementUtils.setObject(statement, 1, (Long) TableUtils.getValue(idRegistration, o));
			}
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void clear(Class<?> tableClass) {
		if (!isConnected()) {
			try {
				connect();
			} catch (ConnectionException e) {
				throw new RuntimeException(e);
			}
		}
		TableRegistration table = getTableRegistration(tableClass);

		if (table == null) {
			throw new UnknownTableException("The table class '" + tableClass.getCanonicalName() + "' is not registered!");
		}
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM ").append(table.getName());

		try {
			PreparedStatement statement = conn.prepareStatement(query.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createTables() {
		for (TableRegistration table : getTables().values()) {
			StringBuilder query = new StringBuilder();
			query.append("CREATE TABLE IF NOT EXISTS ")
				 .append(table.getName())
				 .append(" (")
				 .append(table.getId().getName())
				 .append(" ")
				 .append(MySQLUtil.getMySQLTypeFromClass(table.getId().getType()))
				 .append(" NOT NULL AUTO_INCREMENT PRIMARY KEY, ");
			int count = 0;
			for (FieldRegistration field : table.getFields()) {
				count++;
				String type = null;
				if (field.isSerializable()) {
					type = MySQLUtil.getMySQLTypeFromClass(String.class);
				} else {
					type = MySQLUtil.getMySQLTypeFromClass(field.getType());
				}
				query.append(field.getName())
					 .append(" ")
					 .append(type);
				if (count != table.getFields().size()) {
					query.append(", ");
				}
			}
			query.append(") ");
			try {
				PreparedStatement statement = conn.prepareStatement(query.toString());
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void checkTableStructure(TableRegistration table) {
		// TODO Update table structure
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ")
			 .append(table.getName())
			 .append(" LIMIT 1");
		try {
			ResultSetMetaData meta = conn.prepareStatement(query.toString()).executeQuery().getMetaData();
			Collection<FieldRegistration> fields = table.getFields();
			// <field,alreadyexisting?>
			Map<String,String> redo = new LinkedHashMap<String,String>();
			for (FieldRegistration f : fields){
				boolean found = false;
				String deftype = MySQLUtil.getMySQLTypeFromClass(f.getType());
				for (int i = 1; i <= meta.getColumnCount(); i++){
					if (f.getName().equalsIgnoreCase(meta.getColumnName(i))){
						String type = meta.getColumnTypeName(i);
						if (!deftype.equals(type)){
							redo.put(f.getName(),true + ";" + deftype);
						}
						found = true;
						break;
					}
				}
				if (!found){
					redo.put(f.getName(),false + ";" + deftype);
				}
			}
			for (String s : redo.keySet()){
				StringBuilder q = new StringBuilder();
				q.append("ALTER TABLE ").append(table.getName()).append(" ");
				String[] results = redo.get(s).split(";");
				if (results[0].equalsIgnoreCase("true")){
					q.append("MODIFY COLUMN ").append(s).append(" ").append(results[1]);
				} else {
					q.append("ADD COLUMN ").append(s).append(" ").append(results[1]);
				}
				conn.prepareStatement(q.toString()).executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
