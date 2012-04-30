package com.alta189.simplesave;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

	protected final Map<String, String> properties;
	protected final Driver driver;

	public Configuration(Map<String, String> properties, Driver driver) {
		this.properties = properties;
		this.driver = driver;
	}
	
	public Configuration(Driver driver) {
		this(new HashMap<String, String>(), driver);
	}
	
	public Driver getDriver() {
		return driver;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getProperty(String property) {
		return properties.get(property);
	}

	public Configuration setProperty(String property, String value) {
		properties.put(property, value);
		return this;
	}

	public Configuration removeProperty(String property) {
		properties.remove(property);
		return this;
	}

	public boolean containsProperty(String property) {
		return properties.containsKey(property);
	}
}
