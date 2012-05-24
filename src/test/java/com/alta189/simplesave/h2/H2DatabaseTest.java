package com.alta189.simplesave.h2;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;

public class H2DatabaseTest {

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
		boolean caught = false;
		try {
			db.registerTable(TestClass2.class);
		} catch (TableRegistrationException e) {
			caught = true;
		}
		if (!caught)
			fail("Failed to catch exception with registering duplicate class!");
		try {
			db.connect();
		} catch (ConnectionException e) {
			fail("Failed to connect to database! " + e.toString());
		}
		TestClass one = new TestClass();
		one.setName("Hello World");
		one.setId(1);
		db.save(TestClass.class, one);
		TestClass two = new TestClass();
		two.setName("Cruel World");
		two.setId(2);
		db.save(TestClass.class, two);
		db.getTableRegistration(TestClass.class);
		assert(db.select(TestClass.class).execute().find().size()==2);
		try {
			db.close();
		} catch (ConnectionException e) {
			fail("Failed to close database! " + e.toString());
		}
		tmpfile.delete();
	}
	
	@Table(name = "test")
	public static class TestClass {
		@Id
		private int id;
		
		@Field
		private String name;

		protected int getId() {
			return id;
		}

		protected void setId(int id) {
			this.id = id;
		}

		protected String getName() {
			return name;
		}

		protected void setName(String name) {
			this.name = name;
		}
		
	}
	
	@Table(name = "tEsT")
	public static class TestClass2 {
		@Id
		private int id;
		
		@Field
		private String name;
		
	}

}
