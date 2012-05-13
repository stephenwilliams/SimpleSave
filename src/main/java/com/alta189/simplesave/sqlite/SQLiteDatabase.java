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

	@Override
	public void remove(Class<?> tableClass, Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
