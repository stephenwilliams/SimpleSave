package com.alta189.simplesave.internal;

import com.alta189.simplesave.Table;
import com.alta189.simplesave.internal.reflection.EmptyInjector;
import com.alta189.simplesave.internal.reflection.Injector;

public class TableFactory {

	public static TableRegistration buildTable(Class<?> clazz) {
		// Make sure that the class has the Table annotation
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' does not have the Table annotation");
		}

		// Get the annotation and make sure that 'name' is defined
		Table table = clazz.getAnnotation(Table.class);
		if (table.name() == null || table.name().isEmpty()) {
			throw new TableRegistrationException("Class '\" + clazz.getCanonicalName() + \"' is missing a table name");
		}

		// Check that the class has an empty constructor
		Injector injector = new EmptyInjector();
		try {
			 injector.newInstance(clazz);
		} catch (Exception e) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' does not have an empty constructor", e);
		}

		// Create TableRegistration
		TableRegistration tableRegistration = new TableRegistration(table.name(), clazz);

		// Register fields
		tableRegistration.addFields(FieldFactory.getFields(clazz));

		return tableRegistration;
	}

}
