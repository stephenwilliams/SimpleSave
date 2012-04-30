package com.alta189.simplesave.sqlite;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.query.Query;
import com.alta189.simplesave.query.QueryResult;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends Database {
	private final String uri;
	private Connection connection;
	
	public SQLiteDatabase(String uri) {
		this.uri = uri;	
	}
	
	@Override
	public void connect() throws ConnectionException {
		if (!isConnected()) {
			super.connect();
			try {
				connection = DriverManager.getConnection(uri);
			} catch (SQLException e) {
				throw new ConnectionException(e);
			}
		}
	}
	
	@Override
	public void close() throws ConnectionException {
		if (isConnected()) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new ConnectionException(e);
			}
		}
		
		super.close();
	}

	@Override
	public boolean isConnected() {
		try {
			return connection != null && connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public <T> QueryResult<T> execute(Query<T> query) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void save(Class<?> tableClass, Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}	
}
