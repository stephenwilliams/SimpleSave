package com.alta189.simplesave.sqlite;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Driver;

public class SQLiteConfiguration extends Configuration {
	
	public SQLiteConfiguration(String path) {
		super(Driver.SQLITE);
		properties.put("path", path);
	}
	
	public SQLiteConfiguration() {
		this(null);
	}
	
	public String getPath() {
		return properties.get("path");
	}
	
	public void setPath(String path) {
		properties.put("path", path);
	}
}
