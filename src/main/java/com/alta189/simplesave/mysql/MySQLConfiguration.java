package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Driver;

public class MySQLConfiguration extends Configuration {

	public MySQLConfiguration() {
		super(Driver.MYSQL);

		// Set defaults
		setMySQLPort(MySQLConstants.DefaultPort);
		setMySQLUser(MySQLConstants.DefaultUser);
		setMySQLPassword(MySQLConstants.DefaultPassword);
	}

	public String getMySQLUser() {
		return getProperty(MySQLConstants.MySQLUser);
	}

	public MySQLConfiguration setMySQLUser(String user) {
		setProperty(MySQLConstants.MySQLUser, user);
		return this;
	}

	public String getMySQLPassword() {
		return getProperty(MySQLConstants.MySQLPassword);
	}

	public MySQLConfiguration setMySQLPassword(String password) {
		setProperty(MySQLConstants.MySQLPassword, password);
		return this;
	}

	public String getMySQLHost() {
		return getProperty(MySQLConstants.MySQLHost);
	}

	public MySQLConfiguration setMySQLHost(String host) {
		setProperty(MySQLConstants.MySQLHost, host);
		return this;
	}

	public int getMySQLPort() {
		return Integer.valueOf(getProperty(MySQLConstants.MySQLPort));
	}

	public MySQLConfiguration setMySQLPort(int port) {
		setProperty(MySQLConstants.MySQLPort, Integer.toString(port));
		return this;
	}

	public String getMySQLDatabase() {
		return getProperty(MySQLConstants.MySQLDatabase);
	}

	public MySQLConfiguration setMySQLDatabase(String database) {
		setProperty(MySQLConstants.MySQLDatabase, database);
		return this;
	}

}
