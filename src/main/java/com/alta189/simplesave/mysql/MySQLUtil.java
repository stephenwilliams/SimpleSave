package com.alta189.simplesave.mysql;

public class MySQLUtil {

	public static String getMySQLTypeFromClass(Class<?> clazz) {
		if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
			return "INT";
		} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
			return "LONG";
		} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
			return "DOUBLE";
		} else if (clazz.equals(String.class)) {
			return "VARCHAR(255)";
		}  else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
			return "TINYINT";
		} else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
			return "SMALLINT";
		} else if (clazz.equals(float.class)|| clazz.equals(Float.class)) {
			return "FLOAT";
		} else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
			return "TINYINT";
		}
		return null;
	}



}
