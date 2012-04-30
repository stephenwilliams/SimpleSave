package com.alta189.simplesave.internal;

import com.alta189.simplesave.Id;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.internal.reflection.EmptyInjector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class IdFactory {

	public static IdRegistration getId(Class<?> clazz) throws TableRegistrationException {
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Id.class))
				continue;

			if (Modifier.isStatic(field.getModifiers()))
				throw new TableRegistrationException("The Id cannot be static!");

			Class<?> type = field.getType();
			if (!type.equals(int.class))
				throw new TableRegistrationException("The id is not of type 'int' its class is '" + type.getCanonicalName() + "'");

			// Check if id defaults to 0
			try {
				Object o = new EmptyInjector().newInstance(clazz);
				int id = ((Number)field.get(o)).intValue();
				if (id != 0)
					throw new TableRegistrationException("The id does not default to 0");
			} catch (IllegalAccessException e) {
				throw new TableRegistrationException(e);
			}

			return new IdRegistration(field.getName(), type);
		}
		throw new TableRegistrationException("No field with the @Id annotation");
	}

}
