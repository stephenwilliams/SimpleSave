package com.alta189.simplesave;

public enum Driver {

	MYSQL;
	
	public static Driver getDriver(String value) {
		Driver result = null;
		try {
			result = Driver.valueOf(value.toUpperCase());
		} catch (Exception ignored) {
		}
		return result;
	}
	
}
