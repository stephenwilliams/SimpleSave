package com.alta189.simplesave;

import com.alta189.simplesave.exceptions.UnknownDriverException;
import com.alta189.simplesave.mysql.MySQLConstants;
import com.alta189.simplesave.mysql.MySQLDatabase;

public class DatabaseFactory {

	public static Database createNewDatabase(Configuration configuration) {
		switch (configuration.getDriver()) {
			case MYSQL:
				String mySQLUser = configuration.getProperty(MySQLConstants.MySQLUser);
				if (mySQLUser == null || mySQLUser.isEmpty())
					throw new IllegalArgumentException("MySQLUser is null or empty!");
				String mySQLPass = configuration.getProperty(MySQLConstants.MySQLPassword);
				if (mySQLPass == null) // Password can be empty
					throw new IllegalArgumentException("MySQLUser is null!");
				String mySQLHost = configuration.getProperty(MySQLConstants.MySQLHost);
				if (mySQLHost == null || mySQLHost.isEmpty())
					throw new IllegalArgumentException("MySQLUser is null or empty!");
				String mySQLPort = configuration.getProperty(MySQLConstants.MySQLPort);
				if (mySQLPort == null || mySQLPort.isEmpty())
					throw new IllegalArgumentException("MySQLUser is null or empty!");
				String mySQLDatabase = configuration.getProperty(MySQLConstants.MySQLDatabase);
				if (mySQLDatabase ==  null || mySQLDatabase.isEmpty())
					throw new IllegalArgumentException("MySQLUser is null or empty!");

				StringBuilder mySQLConnUrl = new StringBuilder();
				mySQLConnUrl.append("jdbc:mysql://");
				mySQLConnUrl.append(mySQLHost);
				mySQLConnUrl.append(":");
				mySQLConnUrl.append(mySQLPort);
				mySQLConnUrl.append("/");
				mySQLConnUrl.append(mySQLDatabase);

				return new MySQLDatabase(mySQLConnUrl.toString(), mySQLUser, mySQLPass);
		}
		throw new UnknownDriverException("The driver '" + configuration.getDriver() + "' is unknown");
	}

}
