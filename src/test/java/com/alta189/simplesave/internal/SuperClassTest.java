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
package com.alta189.simplesave.internal;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.h2.H2Configuration;
import com.alta189.simplesave.h2.H2Database;

public class SuperClassTest {

	@Test
	public void test() {
		H2Configuration h2 = new H2Configuration();
		File tmpfile = null;
		try {
			tmpfile = File.createTempFile("h2test_", ".db");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException occured: " + e.toString());
		}
		assertNotNull(tmpfile);
		h2.setDatabase(tmpfile.getAbsolutePath().substring(0, tmpfile.getAbsolutePath().indexOf(".db")));
		H2Database db = (H2Database) DatabaseFactory.createNewDatabase(h2);
		
		try {
			db.registerTable(ChildClass.class);
		} catch (TableRegistrationException e1) {
			e1.printStackTrace();
			fail("Failed to register good table!");
		}
		
		boolean caught = false;
		try {
			db.registerTable(BabyClass.class);
		} catch (TableRegistrationException e1) {
			caught = true;
		}
		
		if (!caught)
			fail("Failed to catch duplicate field registration!");
		
		db.save(ChildClass.class, new ChildClass());
		
		try {
			db.close();
		} catch (ConnectionException e) {
			fail("Failed to close database! " + e.toString());
		}
		tmpfile.delete();
	}
	
	
	public static class ParentClass {
		
		@Field
		public String test;
		
		@Field
		public boolean test2;
		
		
	}
	
	@Table("ChildTest")
	public static class ChildClass extends ParentClass {

		@Id
		public long id;
		
		@Field
		public String othertest;
		
	}
	
	@Table("BabyTest")
	public static class BabyClass extends ParentClass {
		
		@Id
		public long id;
		
		@Field
		public boolean test2;
		
	}

}
