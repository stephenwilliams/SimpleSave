package com.alta189.simplesave.internal;

import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.test.TestTable;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RegistrationTest {

	@Test
	public void testRegistration() {
		TableRegistration test = null;
		try {
			test = TableFactory.buildTable(TestTable.class);
		} catch (TableRegistrationException e) {
			e.printStackTrace();
		}
		assertNotNull("Registration failed", test);
	}

}
