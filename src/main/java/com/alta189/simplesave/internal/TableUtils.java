package com.alta189.simplesave.internal;

import java.lang.reflect.Field;

public class TableUtils {

	public static int getIdValue(TableRegistration table, Object o) {
		IdRegistration idRegistration = table.getId();
		try {
			Field field = o.getClass().getDeclaredField(idRegistration.getName());
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
			return field.getInt(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Long getValueAsLong(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return field.getLong(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Double getValueAsDouble(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return field.getDouble(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String getValueAsString(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return (String) field.get(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Boolean getValueAsBoolean(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return field.getBoolean(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Short getValueAsShort(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return field.getShort(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Float getValueAsFloat(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return field.getFloat(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Byte getValueAsByte(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return field.getByte(o);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String serializeField(FieldRegistration fieldRegistration, Object tableObject) {
		try {
			Field field = tableObject.getClass().getDeclaredField(fieldRegistration.getName());
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
