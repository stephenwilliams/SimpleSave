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

import java.lang.reflect.Field;

public class TableUtils {

	public static Long getIdValue(TableRegistration table, Object o) {
		IdRegistration idRegistration = table.getId();
		try {
			Field field = o.getClass().getDeclaredField(idRegistration.getName());
			field.setAccessible(true);

			if (idRegistration.getType().equals(Integer.class) || idRegistration.getType().equals(int.class)) {
				return new Long((Integer) field.get(o));
			} else {
				return (Long) field.get(o);
			}
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static boolean isValueNull(FieldRegistration fieldRegistration, Object o) {
		Class<?> clazz = o.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldRegistration.getName());
				field.setAccessible(true);
				return field.get(o) == null;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		throw new IllegalArgumentException(new NoSuchFieldException(fieldRegistration.getName()));

	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(FieldRegistration fieldRegistration, Object o) {
		Class<?> clazz = o.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldRegistration.getName());
				field.setAccessible(true);
				return (T) field.get(o);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		throw new IllegalArgumentException(new NoSuchFieldException(fieldRegistration.getName()));
	}

	public static String serializeField(FieldRegistration fieldRegistration, Object tableObject) {
		try {
			Field field = tableObject.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			Object o = field.get(tableObject);
			return SerializedClassBuilder.serialize(fieldRegistration.getClass(), o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Object deserializeField(FieldRegistration fieldRegistration, String data) {
		return SerializedClassBuilder.deserialize(fieldRegistration.getClass(), data);
	}
}
