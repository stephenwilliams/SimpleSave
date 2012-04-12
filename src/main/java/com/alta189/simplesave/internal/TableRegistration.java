package com.alta189.simplesave.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class TableRegistration {

	private final String name;
	private final Class<?> clazz;
	private final Map<String, FieldRegistration> fields = new HashMap<String, FieldRegistration>();

	public TableRegistration(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getTableClass() {
		return clazz;
	}

	public Collection<FieldRegistration> getFields() {
		return fields.values();
	}

	public void addFields(Collection<FieldRegistration> fields) {
		for (FieldRegistration field : fields) {
			addField(field.getName(), field);
		}
	}

	public void addField(String name, FieldRegistration field) {
		fields.put(name.toLowerCase(), field);
	}
}
