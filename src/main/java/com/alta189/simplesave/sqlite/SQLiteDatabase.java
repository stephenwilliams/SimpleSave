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
package com.alta189.simplesave.sqlite;

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

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.UnknownTableException;
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
import com.alta189.simplesave.query.OrderQuery.OrderPair;

public class SQLiteDatabase extends Database {
	private static final String driver = "sqlite";
	private final String uri;
	private Connection connection;

	static {
		DatabaseFactory.registerDatabase(SQLiteDatabase.class);
	}

	public SQLiteDatabase(Configuration config) {
		String path = config.getProperty(SQLiteConstants.Path);
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("Path is null or empty!");
		}
		this.uri = "jdbc:sqlite:" + path;
	}

	public SQLiteDatabase(String uri) {
		this.uri = uri;
	}

	public static String getDriver() {
		return driver;
	}

	@Override
	public void connect() throws ConnectionException {
		if (!isConnected()) {
			try {
				super.connect();
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection(uri);
				createTables();
				if (checkTableOnRegistration()) {
					for (TableRegistration t : getTableRegistrations()){
						checkTableStructure(t);
					}
				}
			} catch (ClassNotFoundException e) {
				throw new ConnectionException("Could not find the SQLite JDBC driver!", e);
			} catch (SQLException sql) {
				throw new ConnectionException(sql);
			}
		}
	}

	@Override
	public boolean isConnected() {
		try {
			return connection != null && !connection.isClosed();
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

			// Prepare the query
			switch (query.getType()) {
				case SELECT:
					SelectQuery select = (SelectQuery) query;
					TableRegistration table = getTableRegistration(select.getTableClass());
					PreparedStatement statement = null;
					StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(table.getName()).append(" ");
					if (!select.where().getEntries().isEmpty()) {
						queryBuilder.append("WHERE ");
						int iter = 0;
						for (Object o : select.where().getEntries()) {
							iter++;
							if (!(o instanceof WhereEntry)) {
								continue;
							}

							WhereEntry entry = (WhereEntry) o;
							if (entry.getPrefix() != null && !entry.getPrefix().isEmpty()) {
								queryBuilder.append(entry.getPrefix());
							}
							queryBuilder.append(entry.getField());
							switch (entry.getComparator()) {
								case EQUAL:
									queryBuilder.append("==? ");
									break;
								case NOT_EQUAL:
									queryBuilder.append("!=? ");
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
									queryBuilder.append("<=?");
									break;
								case CONTAINS:
									queryBuilder.append(" LIKE ?");
									break;
							}
							if (entry.getSuffix() != null && !entry.getSuffix().isEmpty()) {
								queryBuilder.append(entry.getSuffix());
							}
							if (iter != select.where().getEntries().size()) {
								queryBuilder.append(entry.getOperator().name())
										.append(" ");
							}
						}
						if (!select.order().getPairs().isEmpty()) {
							queryBuilder.append("ORDER BY ");
							int track = 0;
							for (Object pair : select.order().getPairs()) {
								track++;
								if (!(pair instanceof OrderPair)) {
									throw new InternalError("Internal Error: Uncastable Object to OrderPair!");
								}
								OrderPair order = (OrderPair) pair;
								queryBuilder.append(order.column).append(" ").append(order.order.name());
								if (track == select.order().getPairs().size()) {
									queryBuilder.append(" ");
								} else { queryBuilder.append(", "); }
							}
						}
						if (select.limit().getLimit() != null) {
							queryBuilder.append("LIMIT ");
							if (select.limit().getStartFrom() != null) {
								queryBuilder.append(select.limit().getStartFrom()).append(", ");
							}
							queryBuilder.append(select.limit().getLimit()).append(" ");
						}

						statement = connection.prepareStatement(queryBuilder.toString());
						iter = 0;
						for (Object o : select.where().getEntries()) {
							iter++;
							if (!(o instanceof WhereEntry)) {
								continue;
							}

							WhereEntry entry = (WhereEntry) o;
							if (entry.getComparator() == Comparator.CONTAINS) {
								statement.setString(iter, "%" + entry.getComparison().getValue().toString() + "%");
							} else {
								PreparedStatementUtils.setObject(statement, iter, entry.getComparison().getValue());
							}
						}
					}

					// Execute and return
					if (statement == null) {
						if (!select.order().getPairs().isEmpty()) {
							queryBuilder.append("ORDER BY ");
							int track = 0;
							for (Object pair : select.order().getPairs()) {
								track++;
								if (!(pair instanceof OrderPair)) {
									throw new InternalError("Internal Error: Uncastable Object to OrderPair!");
								}
								OrderPair order = (OrderPair) pair;
								queryBuilder.append(order.column).append(" ").append(order.order.name());
								if (track == select.order().getPairs().size()) {
									queryBuilder.append(" ");
								} else { queryBuilder.append(", "); }
							}
						}
						if (select.limit().getLimit() != null) {
							queryBuilder.append("LIMIT ");
							if (select.limit().getStartFrom() != null) {
								queryBuilder.append(select.limit().getStartFrom()).append(", ");
							}
							queryBuilder.append(select.limit().getLimit()).append(" ");
						}
						statement = connection.prepareStatement(queryBuilder.toString());
					}
					ResultSet results = statement.executeQuery();
					QueryResult<T> result = new QueryResult<T>(ResultSetUtils.buildResultList(table, (Class<T>) table.getTableClass(), results));
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

		StringBuilder buffer = new StringBuilder();
		long id = TableUtils.getIdValue(table, o);
		if (id == 0) {
			buffer.append("INSERT INTO ")
					.append(table.getName())
					.append(" (");
			StringBuilder values = new StringBuilder();
			values.append("VALUES ( ");
			int iter = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				iter++;
				buffer.append(fieldRegistration.getName());
				values.append("?");
				if (iter == table.getFields().size()) {
					buffer.append(") ");
					values.append(")");
				} else {
					buffer.append(", ");
					values.append(", ");
				}
			}
			buffer.append(values.toString());
		} else {
			buffer.append("UPDATE ")
					.append(table.getName())
					.append(" SET ");
			int iter = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				iter++;
				buffer.append(fieldRegistration.getName())
						.append("=?");
				if (iter != table.getFields().size()) {
					buffer.append(", ");
				}
			}
			buffer.append(" WHERE ")
					.append(table.getId().getName())
					.append(" = ?");
		}

		try {
			PreparedStatement statement;
			if (id == 0) {
				statement = connection.prepareStatement(buffer.toString(), Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = connection.prepareStatement(buffer.toString());
			}
			int i = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				i++;
				if (fieldRegistration.isSerializable()) {
					PreparedStatementUtils.setObject(statement, i, o);
				} else {
					if (fieldRegistration.getType().equals(int.class) || fieldRegistration.getType().equals(Integer.class)) {
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
						throw new RuntimeException(e);
					}
				}
			}
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
		if (id == 0) {
			throw new IllegalArgumentException("Object was never inserted into database!");
		}
		query.append("DELETE FROM ")
				.append(table.getName())
				.append(" WHERE ")
				.append(table.getId().getName())
				.append("=?");

		try {
			PreparedStatement statement = connection.prepareStatement(query.toString());

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
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createTables() {
		// Query - "CREATE TABLE IF NOT EXISTS <table> (<field> <type>...)"
		for (TableRegistration table : getTables().values()) {
			StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table.getName() + " (");
			int iter = 0;
			Collection<FieldRegistration> fields = table.getFields();
			builder.append("'").append(table.getId().getName()).append("'")
					.append(" ").append(SQLiteUtil.getSQLiteTypeFromClass(table.getId().getType()))
					.append(" NOT NULL PRIMARY KEY AUTOINCREMENT").append(",");
			for (FieldRegistration field : fields) {
				iter++;
				builder.append("'").append(field.getName()).append("'")
						.append(" ").append(SQLiteUtil.getSQLiteTypeFromClass(field.getType()));
				if (iter < fields.size()) {
					builder.append(",");
				} else {
					builder.append(")");
				}
			}
			try {
				PreparedStatement statement = connection.prepareStatement(builder.toString());
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
			ResultSetMetaData meta = connection.prepareStatement(query.toString()).executeQuery().getMetaData();
			Collection<FieldRegistration> fields = table.getFields();
			// <field,alreadyexisting?>
			Map<String,String> redo = new LinkedHashMap<String,String>();
			for (FieldRegistration f : fields){
				boolean found = false;
				String deftype = SQLiteUtil.getSQLiteTypeFromClass(f.getType());
				for (int i = 1; i <= meta.getColumnCount(); i++){
					if (f.getName().equalsIgnoreCase(meta.getColumnName(i))){
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
				String[] results = redo.get(s).split(";");
				q.append("ALTER TABLE ").append(table.getName()).append(" ");
				q.append("ADD COLUMN ").append(s).append(" ").append(results[1]);
				connection.prepareStatement(q.toString()).executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
