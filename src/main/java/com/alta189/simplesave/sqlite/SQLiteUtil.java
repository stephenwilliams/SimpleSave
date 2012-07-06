package com.alta189.simplesave.sqlite;

import java.io.Serializable;

public class SQLiteUtil {
	public static String getSQLiteTypeFromClass(Class<?> clazz) {
		if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
			return "INTEGER";
		} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
			return "INTEGER";
		} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
			return "DOUBLE";
		} else if (clazz.equals(String.class)) {
			return "TEXT";
		} else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
			return "TINYINT";
		} else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
			return "SMALLINT";
		} else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
			return "FLOAT";
		} else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
			return "TINYINT";
		}
		Class<?> checkclazz = clazz;
		while (checkclazz!=null){
			for (Class<?> i : checkclazz.getInterfaces())
				if (i.equals(Serializable.class))
					return "BLOB";
			checkclazz = checkclazz.getSuperclass();
		}
		return null;
	}
}
