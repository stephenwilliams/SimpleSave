package com.alta189.simplesave.internal;

import com.alta189.simplesave.exceptions.FieldRegistrationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldFactory {

	public static List<FieldRegistration> getFields(Class<?> clazz) throws FieldRegistrationException {
		final List<FieldRegistration> fields = new ArrayList<FieldRegistration>();
		for (Field field : clazz.getDeclaredFields()) {
			FieldRegistration fieldRegistration = getField(field);
			if (fieldRegistration != null) {
				fields.add(fieldRegistration);
			}
		}
		return fields;
	}

	public static FieldRegistration getField(Field field) throws FieldRegistrationException {
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
		if (type.equals(int.class) || type.equals(Integer.class)) {
			return true;
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return true;
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return true;
		} else if (type.equals(String.class)) {
			return true;
		}  else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return true;
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			return true;
		} else if (type.equals(float.class)|| type.equals(Float.class)) {
			return true;
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			return true;
		}
		return false;
	}

}
