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

import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.internal.TableFactory;
import com.alta189.simplesave.internal.TableRegistration;
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
