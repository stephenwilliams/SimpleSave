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

import com.alta189.simplesave.Id;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.internal.reflection.EmptyInjector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class IdFactory {
	public static IdRegistration getId(Class<?> clazz) throws TableRegistrationException {
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Id.class)) {
				continue;
			}

			if (Modifier.isStatic(field.getModifiers())) {
				throw new TableRegistrationException("The Id cannot be static!");
			}

			Class<?> type = field.getType();
			if (!type.equals(int.class)) {
				throw new TableRegistrationException("The id is not of type 'int' its class is '" + type.getCanonicalName() + "'");
			}

			// Check if id defaults to 0
			try {
				Object o = new EmptyInjector().newInstance(clazz);
				field.setAccessible(true);
				int id = ((Number) field.get(o)).intValue();
				if (id != 0) {
					throw new TableRegistrationException("The id does not default to 0");
				}
			} catch (IllegalAccessException e) {
				throw new TableRegistrationException(e);
			}

			return new IdRegistration(field.getName(), type);
		}
		throw new TableRegistrationException("No field with the @Id annotation");
	}
}
