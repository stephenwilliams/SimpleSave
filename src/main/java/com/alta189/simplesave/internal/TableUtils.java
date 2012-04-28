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

	public static String getValueAsString(FieldRegistration fieldRegistration, Object o) {
		try {
			Field field = o.getClass().getDeclaredField(fieldRegistration.getName());
			return field.get(o).toString();
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
