package com.alta189.simplesave.internal;

import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.FieldRegistrationException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.internal.reflection.EmptyInjector;
import com.alta189.simplesave.internal.reflection.Injector;

import java.util.regex.Pattern;

public class TableFactory {

	public static TableRegistration buildTable(Class<?> clazz) throws TableRegistrationException {
		// Make sure that the class has the Table annotation
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' does not have the Table annotation");
		}

		// Get the annotation and make sure that 'name' is defined
		Table table = clazz.getAnnotation(Table.class);
		if (table.name() == null || table.name().isEmpty()) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' is missing a table name");
		}

		// Check that 'name' is only made up of allowed characters (Alphanumeric and '_')
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		if (!pattern.matcher(table.name()).find()) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' table name has illegal characters in it. The name is limited to alphanumeric characters and '_'");
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
		try {
			tableRegistration.addFields(FieldFactory.getFields(clazz));
		} catch (FieldRegistrationException e) {
			throw new TableRegistrationException(e);
		}

		return tableRegistration;
	}

}
