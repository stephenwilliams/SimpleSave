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

public class SerializableTest {

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
			db.registerTable(TestClass.class);
		} catch (TableRegistrationException e) {
			e.printStackTrace();
			fail("Exception occured too early! " + e.toString());
		}

		TestClass testone = new TestClass();
		db.save(testone);
		TestClass testtwo = new TestClass(null);
		db.save(testtwo);
		
		Parent p = db.select(TestClass.class).where().contains("test", "moo").execute().findOne().test2;
		assertNotNull(p);
		assertTrue(p instanceof NObject);
		
		Parent d = db.select(TestClass.class).where().contains("test", "test").execute().findOne().test2;
		assertNotNull(d);
		assertTrue(d instanceof SObject);
		
		try {
			db.close();
		} catch (ConnectionException e) {
			fail("Failed to close database! " + e.toString());
		}
		tmpfile.delete();
	}
	
	@Table("test")
	public static class TestClass {
		
		@Id
		public long id;
		
		@Field
		public String test;
		
		@Field
		public Parent test2;
		
		public TestClass(){
			test = "test";
			test2 = new SObject();
		}
		
		public TestClass(Object herp){
			test = "moo";
			test2 = new NObject();
		}
		
	}
	
	public static class Parent implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6227625777872585932L;
		
	}
	
	public static class SObject extends Parent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7265250405512469151L;
		
	}
	
	public static class NObject extends Parent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3577022339213440085L;
		
	}

}
