package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Driver;

public class MySQLConfiguration extends Configuration {
	
	public MySQLConfiguration(String username, int port, String password) {
		super(Driver.MYSQL);
		properties.put(MySQLConstants.User, username);
		properties.put(MySQLConstants.Password, password);
		properties.put(MySQLConstants.Port, Integer.toString(port));
	}
	
	public MySQLConfiguration(String username, int port) {
		this(username, port, "");
	}
	
	public MySQLConfiguration(String username) {
		this(username, 3306);
	}

	public String getPassword() {
		return properties.get(MySQLConstants.Password);
	}

	public void setPassword(String password) {
		properties.put(MySQLConstants.Password, password);
	}

	public String getHost() {
		return properties.get(MySQLConstants.Host);
	}

	public void setHost(String host) {
		properties.put(MySQLConstants.Host, host);
	}

	public int getPort() {
		return Integer.valueOf(properties.get(MySQLConstants.Port));
	}

	public void setPort(int port) {
		setProperty(MySQLConstants.Port, Integer.toString(port));
	}

	public String getDatabase() {
		return properties.get(MySQLConstants.Database);
	}

	public void setDatabase(String database) {
		properties.put(MySQLConstants.Database, database);
	}
}
