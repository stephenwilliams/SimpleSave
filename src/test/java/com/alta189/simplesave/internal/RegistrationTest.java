package com.alta189.simplesave.internal;

import com.alta189.simplesave.test.TestTable;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RegistrationTest {

	@Test
	public void testRegistration() {
		TableRegistration test = TableFactory.buildTable(TestTable.class);
		assertNotNull("Registration failed", test);
	}

}
