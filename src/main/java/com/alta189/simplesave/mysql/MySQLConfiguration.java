package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Driver;

public class MySQLConfiguration extends Configuration {
	
	public MySQLConfiguration() {
		super(Driver.MYSQL);

		// Set defaults
		setUser(MySQLConstants.DefaultUser);
		setPassword(MySQLConstants.DefaultPass);
		setPort(MySQLConstants.DefaultPort);
	}

	public String getUser() {
		return getProperty(MySQLConstants.User);
	}
	
	public MySQLConfiguration setUser(String user) {
		setProperty(MySQLConstants.User, user);
		return this;
	}

	public String getPassword() {
		return getProperty(MySQLConstants.Password);
	}

	public MySQLConfiguration setPassword(String password) {
		setProperty(MySQLConstants.Password, password);
		return this;
	}

	public String getHost() {
		return getProperty(MySQLConstants.Host);
	}

	public MySQLConfiguration setHost(String host) {
		setProperty(MySQLConstants.Host, host);
		return this;
	}

	public int getPort() {
		return Integer.valueOf(getProperty(MySQLConstants.Port));
	}

	public MySQLConfiguration setPort(int port) {
		setProperty(MySQLConstants.Port, Integer.toString(port));
		return this;
	}

	public String getDatabase() {
		return getProperty(MySQLConstants.Database);
	}

	public MySQLConfiguration setDatabase(String database) {
		setProperty(MySQLConstants.Database, database);
		return this;
	}

}
