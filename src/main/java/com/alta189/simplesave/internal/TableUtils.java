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

	public static int getIdValue(TableRegistration table, Object o) {
		IdRegistration idRegistration = table.getId();
		try {
			Field field = o.getClass().getDeclaredField(idRegistration.getName());
			field.setAccessible(true);
			return ((Number)field.get(o)).intValue();
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Integer getValueAsInteger(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return field.getInt(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Long getValueAsLong(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return field.getLong(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Double getValueAsDouble(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return field.getDouble(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String getValueAsString(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return (String) field.get(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Boolean getValueAsBoolean(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return field.getBoolean(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Short getValueAsShort(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return field.getShort(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Float getValueAsFloat(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return field.getFloat(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Byte getValueAsByte(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			field.setAccessible(true);
			return field.getByte(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
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
