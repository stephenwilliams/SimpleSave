package com.alta189.simplesave.internal;

import com.alta189.simplesave.Id;
import com.alta189.simplesave.exceptions.TableRegistrationException;

import java.lang.reflect.Field;

public class IdFactory {

	public static IdRegistration getId(Class<?> clazz) throws TableRegistrationException {
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Id.class))
				continue;

			Class<?> type = field.getType();
			if (type.equals(int.class))
				throw new TableRegistrationException("The id is not of type 'int'");

			return new IdRegistration(field.getName(), type);
		}
		throw new TableRegistrationException("No field with the @Id annotation");
	}

}
