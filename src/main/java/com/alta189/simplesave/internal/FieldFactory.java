package com.alta189.simplesave.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FieldFactory {

	public static List<FieldRegistration> getFields(Class<?> clazz) {
		final List<FieldRegistration> fields = new ArrayList<FieldRegistration>();
		for (Field field : clazz.getDeclaredFields()) {
			FieldRegistration fieldRegistration = getField(field);
			if (fieldRegistration != null) {
				fields.add(fieldRegistration);
			}
		}
		return fields;
	}

	public static FieldRegistration getField(Field field) {
		// Check if the field has the Field annotation
		com.alta189.simplesave.Field fieldAnnotation = getFieldAnnotation(field);
		if (fieldAnnotation == null) {
			return null;
		}

		Class<?> type = field.getType();

		// Check if the field has a valid type
		if (validType(type)) {
			return new FieldRegistration(field.getName(), type);
		} else if (SerializedClassBuilder.validClass(type)) {
			return new FieldRegistration(field.getName(), type, true);
		} else {
			throw new FieldRegistrationException("The type '" + type.getCanonicalName() + "' is not a valid type");
		}
	}

	public static com.alta189.simplesave.Field getFieldAnnotation(Field field) {
		com.alta189.simplesave.Field fieldAnnotation = null;
		for (Annotation annotation : field.getDeclaredAnnotations()) {
			if (annotation instanceof com.alta189.simplesave.Field) {
				fieldAnnotation = (com.alta189.simplesave.Field) annotation;
				break;
			}
		}
		return fieldAnnotation;
	}

	public static boolean hasFieldAnnotation(Field field) {
		for (Annotation annotation : field.getDeclaredAnnotations()) {
			if (annotation.getClass().equals(com.alta189.simplesave.Field.class)) {
				return true;
			}
		}
		return false;
	}

	public static boolean validType(Class<?> type) {
		return true;
	}

}
