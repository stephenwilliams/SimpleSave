package com.alta189.simplesave.internal;

import com.alta189.simplesave.Id;

import java.lang.reflect.Field;

public class IdFactory {

	public static IdRegistration getId(Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Id.class))
				continue;

			Class<?> type = field.getType();

		}
		return null;
	}

}
