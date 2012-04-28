package com.alta189.simplesave.internal;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementUtils {

	public static void setObject(PreparedStatement statement, int index, Object o) throws SQLException {
		Class clazz = o.getClass();
		if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
			statement.setInt(index, ((Number) o).intValue());
		} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
			statement.setLong(index, ((Number) o).longValue());
		} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
			statement.setDouble(index, ((Number) o).doubleValue());
		} else if (clazz.equals(String.class)) {
			statement.setString(index, (String) o);
		}  else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
			statement.setInt(index, ((Boolean) o) ? 1 : 0);
		} else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
			statement.setShort(index, ((Number) o).shortValue());
		} else if (clazz.equals(float.class)|| clazz.equals(Float.class)) {
			statement.setFloat(index, ((Number) o).floatValue());
		} else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
			statement.setByte(index, ((Number) o).byteValue());
		}
	}

}
