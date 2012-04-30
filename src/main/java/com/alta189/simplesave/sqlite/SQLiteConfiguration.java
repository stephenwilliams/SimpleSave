package com.alta189.simplesave.sqlite;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Driver;

public class SQLiteConfiguration extends Configuration {

	public SQLiteConfiguration() {
		super(Driver.SQLITE);
	}

	public SQLiteConfiguration(String path) {
		super(Driver.SQLITE);
		properties.put(SQLiteConstants.Path, path);
	}
	
	public String getPath() {
		return properties.get(SQLiteConstants.Path);
	}
	
	public void setPath(String path) {
		properties.put(SQLiteConstants.Path, path);
	}
}
