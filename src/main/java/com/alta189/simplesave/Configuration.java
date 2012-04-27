package com.alta189.simplesave;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

	private Map<String, String> properties = new HashMap<String, String>();
	private final Driver driver;

	public Configuration(Driver driver) {
		this.driver = driver;
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
