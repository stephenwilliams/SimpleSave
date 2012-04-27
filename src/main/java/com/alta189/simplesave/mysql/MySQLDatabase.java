package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.internal.TableRegistration;
import com.alta189.simplesave.query.Query;
import com.alta189.simplesave.query.QueryResult;
import com.alta189.simplesave.query.SelectQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase extends Database {

	private final String connUrl;
	private final String user;
	private final String pass;
	private Connection conn;

	public MySQLDatabase(String connUrl, String user, String pass) {
		this.connUrl = connUrl;
		this.user = pass;
		this.pass = pass;
	}

	@Override
	public void connect() throws ConnectionException {
		if (!isConnected()) {
			super.connect();
			try {
				conn = DriverManager.getConnection(connUrl, user, pass);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws ConnectionException {
		if (isConnected()) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
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
	public SelectQuery select(Class<?> tableClass) {
		return null;
	}

	@Override
	public QueryResult execute(Query query) {
		return null;
	}

	@Override
	public void save(Class<?> tableClass, Object o) {
		if (!tableClass.isAssignableFrom(o.getClass()))
			throw new IllegalArgumentException("The provided table class and save objects classes were not compatible.");

		TableRegistration table = getTableRegistration(tableClass);
	}
}
