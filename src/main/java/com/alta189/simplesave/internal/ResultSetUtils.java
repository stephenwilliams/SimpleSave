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

import com.alta189.simplesave.internal.reflection.EmptyInjector;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetUtils {
	private static final EmptyInjector injector = new EmptyInjector();

	public static <E> List<E> buildResultList(TableRegistration table, Class<E> clazz, ResultSet set) {
		List<E> result = new ArrayList<E>();
		try {
			while (set.next()) {
				result.add(buildResult(table, clazz, set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static <E> E buildResult(TableRegistration table, Class<E> clazz, ResultSet set) {
		E result = (E) injector.newInstance(clazz);
		setField(table.getId(), result, set);

		for (FieldRegistration field : table.getFields()) {
			setField(field, result, set);
		}

		return result;
	}

	private static <E> void setField(FieldRegistration fieldRegistration, E object, ResultSet set) {
		try {
			Field field = object.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			if (fieldRegistration.isSerializable()) {
				String result = set.getString(fieldRegistration.getName());
				field.set(object, TableUtils.deserializeField(fieldRegistration, result));
			} else {
				if (fieldRegistration.getType().equals(int.class) || fieldRegistration.getType().equals(Integer.class)) {
					field.setInt(object, set.getInt(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(long.class) || fieldRegistration.getType().equals(Long.class)) {
					field.setLong(object, set.getLong(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(double.class) || fieldRegistration.getType().equals(Double.class)) {
					field.setDouble(object, set.getDouble(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(String.class)) {
					field.set(object, set.getString(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(boolean.class) || fieldRegistration.getType().equals(Boolean.class)) {
					int i = set.getInt(fieldRegistration.getName());
					if (i == 1) {
						field.setBoolean(object, true);
					} else {
						field.setBoolean(object, false);
					}
				} else if (fieldRegistration.getType().equals(short.class) || fieldRegistration.getType().equals(Short.class)) {
					field.setShort(object, set.getShort(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(float.class) || fieldRegistration.getType().equals(Float.class)) {
					field.setFloat(object, set.getFloat(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(byte.class) || fieldRegistration.getType().equals(Byte.class)) {
					field.setByte(object, set.getByte(fieldRegistration.getName()));
				}
			}
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
