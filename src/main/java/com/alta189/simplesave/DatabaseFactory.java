package com.alta189.simplesave;

import com.alta189.simplesave.exceptions.UnknownDriverException;
import com.alta189.simplesave.mysql.MySQLConstants;
import com.alta189.simplesave.mysql.MySQLDatabase;
import com.alta189.simplesave.sqlite.SQLiteConstants;
import com.alta189.simplesave.sqlite.SQLiteDatabase;

public class DatabaseFactory {

	public static Database createNewDatabase(Configuration config) {
		switch (config.getDriver()) {
			case MYSQL:
				String mySQLUser = config.getProperty(MySQLConstants.User);
				if (mySQLUser == null || mySQLUser.isEmpty()) {
					throw new IllegalArgumentException("Username is null or empty!");
				}
				
				String mySQLPass = config.getProperty(MySQLConstants.Password);
				if (mySQLPass == null) { // Password can be empty
					throw new IllegalArgumentException("Password is null!");
				}
				
				String mySQLHost = config.getProperty(MySQLConstants.Host);
				if (mySQLHost == null || mySQLHost.isEmpty()) {
					throw new IllegalArgumentException("Host is null or empty!");
				}
				
				String mySQLPort = config.getProperty(MySQLConstants.Port);
				if (mySQLPort == null || mySQLPort.isEmpty()) {
					throw new IllegalArgumentException("Port is null or empty!");
				}
				
				String mySQLDatabase = config.getProperty(MySQLConstants.Database);
				if (mySQLDatabase ==  null || mySQLDatabase.isEmpty()) {
					throw new IllegalArgumentException("Database is null or empty!");
				}

				StringBuilder mySQLConnUrl = new StringBuilder();
				mySQLConnUrl.append("jdbc:mysql://");
				mySQLConnUrl.append(mySQLHost);
				mySQLConnUrl.append(":");
				mySQLConnUrl.append(mySQLPort);
				mySQLConnUrl.append("/");
				mySQLConnUrl.append(mySQLDatabase);
				return new MySQLDatabase(mySQLConnUrl.toString(), mySQLUser, mySQLPass);
			
			case SQLITE:
				String path = config.getProperty(SQLiteConstants.Path);
				if (path == null || path.isEmpty()) {
					throw new IllegalArgumentException("Path is null or empty!");
				}
				
				String uri = "jdbc:sqlite:" + path;
				return new SQLiteDatabase(uri);
				
			default: 
				throw new UnknownDriverException("The driver '" + config.getDriver() + "' is unknown");	
		}
	}
}
