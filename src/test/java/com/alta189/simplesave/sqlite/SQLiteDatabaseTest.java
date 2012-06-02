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

import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.ConnectionException;

import org.junit.Test;
import static org.junit.Assert.fail;

public class SQLiteDatabaseTest {
	
	@Test
	public void test() {
		SQLiteConfiguration config = new SQLiteConfiguration();
		config.setPath("test.db");
		SQLiteDatabase db = (SQLiteDatabase) DatabaseFactory.createNewDatabase(config);
		try {
			db.connect();
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}
	}
	
	@Table("test")
	private class TestTable {
		@Id
		private int id;
		
		@Field
		private String name;
	}
}
