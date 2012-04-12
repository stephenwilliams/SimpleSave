package com.alta189.simplesave.internal;

public class FieldRegistration {

	private final String name;
	private final Class<?> type;
	private final boolean serializable;

	public FieldRegistration(String name, Class<?> type) {
		this(name, type, false);
	}

	public FieldRegistration(String name, Class<?> type, boolean serializable) {
		this.name = name;
		this.type = type;
		this.serializable = serializable;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isSerializable() {
		return serializable;
	}

}
