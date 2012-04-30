package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.NotConnectedException;
import com.alta189.simplesave.exceptions.UnknownTableException;
import com.alta189.simplesave.internal.FieldRegistration;
import com.alta189.simplesave.internal.PreparedStatementUtils;
import com.alta189.simplesave.internal.ResultSetUtils;
import com.alta189.simplesave.internal.TableRegistration;
import com.alta189.simplesave.internal.TableUtils;
import com.alta189.simplesave.query.Query;
import com.alta189.simplesave.query.QueryResult;
import com.alta189.simplesave.query.SelectQuery;
import com.alta189.simplesave.query.WhereEntry;

import java.sql.*;

public class MySQLDatabase extends Database {

	private final String connUrl;
	private final String user;
	private final String pass;
	private Connection conn;

	public MySQLDatabase(String connUrl, String user, String pass) {
		this.connUrl = connUrl;
		this.user = user;
		this.pass = pass;
	}

	@Override
	public void connect() throws ConnectionException {
		if (!isConnected()) {
			super.connect();
			try {
				conn = DriverManager.getConnection(connUrl, user, pass);
				createTables();
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
		}
		super.close();
	}

	@Override
	public boolean isConnected() {
		try {
			return conn != null && !conn.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public <T> QueryResult<T> execute(Query<T> query) {
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
							if (!(o instanceof WhereEntry))
								throw new InternalError("Something has gone very wrong!");

							WhereEntry entry = (WhereEntry) o;
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
									queryBuilder.append("LIKE{%?%} ");
									break;
							}
							if (count != selectQuery.where().getEntries().size()) {
								queryBuilder.append(entry.getComparator().name())
										.append(" ");
							}
						}
						statement = conn.prepareStatement(queryBuilder.toString());
						count = 0;
						for (Object o : selectQuery.where().getEntries()) {
							count++;
							if (!(o instanceof WhereEntry))
								throw new InternalError("Something has gone very wrong!");

							WhereEntry entry = (WhereEntry) o;
							PreparedStatementUtils.setObject(statement, count, entry.getComparison().getValue());
						}
					}
					if (statement == null)
						statement = conn.prepareStatement(queryBuilder.toString());
					ResultSet set = statement.executeQuery();
					return new QueryResult<T>(ResultSetUtils.buildResultList(table, (Class<T>) table.getTableClass(), set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public void save(Class<?> tableClass, Object o) {
		if (!isConnected())
			throw new NotConnectedException("Cannot save when not connected!");
		if (!tableClass.isAssignableFrom(o.getClass()))
			throw new IllegalArgumentException("The provided table class and save objects classes were not compatible.");

		TableRegistration table = getTableRegistration(tableClass);

		if (table == null)
			throw new UnknownTableException("The table class '" + tableClass.getCanonicalName() + "' is not registered!");

		StringBuilder query = new StringBuilder();
		int id = TableUtils.getIdValue(table, o);
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
				count ++;
				query.append(fieldRegistration.getName())
						.append("=?");
				if (count != table.getFields().size()) {
					query.append(", ");
				}
			}
			query.append(" WHERE ")
					.append(table.getId().getName())
					.append("=")
					.append(id);
		}

		try {
			PreparedStatement statement = conn.prepareStatement(query.toString());
			int i = 0;
			for (FieldRegistration fieldRegistration : table.getFields()) {
				i++;
				if (fieldRegistration.isSerializable()) {
					PreparedStatementUtils.setObject(statement, i, o);
				} else {
					if (fieldRegistration.getType().equals(int.class) || fieldRegistration.getType().equals(Integer.class)) {
						PreparedStatementUtils.setObject(statement, i, TableUtils.getValueAsInteger(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(long.class) || fieldRegistration.getType().equals(Long.class)) {
						PreparedStatementUtils.setObject(statement, i, TableUtils.getValueAsLong(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(double.class) || fieldRegistration.getType().equals(Double.class)) {
						PreparedStatementUtils.setObject(statement, i, TableUtils.getValueAsDouble(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(String.class)) {
						PreparedStatementUtils.setObject(statement, i, TableUtils.getValueAsString(fieldRegistration, o));
					}  else if (fieldRegistration.getType().equals(boolean.class) || fieldRegistration.getType().equals(Boolean.class)) {
						boolean value =  TableUtils.getValueAsBoolean(fieldRegistration, o);
						if (value) {
							PreparedStatementUtils.setObject(statement, i, 1);
						} else {
							PreparedStatementUtils.setObject(statement, i, 0);
						}
					} else if (fieldRegistration.getType().equals(short.class) || fieldRegistration.getType().equals(Short.class)) {
						PreparedStatementUtils.setObject(statement, i, TableUtils.getValueAsShort(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(float.class)|| fieldRegistration.getType().equals(Float.class)) {
						PreparedStatementUtils.setObject(statement, i, TableUtils.getValueAsFloat(fieldRegistration, o));
					} else if (fieldRegistration.getType().equals(byte.class) || fieldRegistration.getType().equals(Byte.class)) {
						PreparedStatementUtils.setObject(statement, i, TableUtils.getValueAsByte(fieldRegistration, o));
					}
				}
			}

			System.out.println(statement.toString());
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
			System.out.println("query = " + query.toString());
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
			for (int i = 1; i <= meta.getColumnCount(); i++) {

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
