package com.alta189.simplesave.mysql;

public class MySQLUtil {

	public static String getMySQLTypeFromClass(Class<?> clazz) {
		// TODO Clean this up a bit
		if (clazz.equals(int.class)) {
			return "INT";
		} else if (clazz.equals(Integer.class)) {
			return "INT";
		} else if (clazz.equals(long.class)) {
			return "BIGINT";
		} else if (clazz.equals(Long.class)) {
			return "BIGINT";
		} else if (clazz.equals(Double.class)) {
			return "DOUBLE";
		} else if (clazz.equals(double.class)) {
			return "DOUBLE";
		} else if (clazz.equals(String.class)) {
			return "VARCHAR";
		}  else if (clazz.equals(Boolean.class)) {
			return "TINYINT";
		} else if (clazz.equals(boolean.class)) {
			return "TINYINT";
		} else if (clazz.equals(short.class)) {
			return "SMALLINT";
		} else if (clazz.equals(Short.class)) {
			return "SMALLINT";
		} else if (clazz.equals(Float.class)) {
			return "FLOAT";
		} else if (clazz.equals(float.class)) {
			return "FLOAT";
		} else if (clazz.equals(byte.class)) {
			return "FLOAT";
		} else if (clazz.equals(Byte.class)) {
			return "FLOAT";
		}
		return null;
	}

}
