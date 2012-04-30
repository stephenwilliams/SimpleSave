package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Driver;

public class MySQLConfiguration extends Configuration {
	
	public MySQLConfiguration(String username, int port, String password) {
		super(Driver.MYSQL);
		properties.put("username", username);
		properties.put("password", password);
		properties.put("port", Integer.toString(port));
	}
	
	public MySQLConfiguration(String username, int port) {
		this(username, port, "");
	}
	
	public MySQLConfiguration(String username) {
		this(username, 3306);
	}

	public String getPassword() {
		return properties.get("password");
	}

	public void setPassword(String password) {
		properties.put("password", password);
	}

	public String getHost() {
		return properties.get("host");
	}

	public void setHost(String host) {
		properties.put("host", host);
	}

	public int getPort() {
		return Integer.valueOf(properties.get("port"));
	}

	public void setPort(int port) {
		setProperty("port", Integer.toString(port));
	}

	public String getDatabase() {
		return properties.get("database");
	}

	public void setDatabase(String database) {
		properties.put("database", database);
	}
}
