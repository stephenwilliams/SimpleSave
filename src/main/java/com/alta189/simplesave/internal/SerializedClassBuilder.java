package com.alta189.simplesave.internal;

import com.alta189.simplesave.exceptions.SerializeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SerializedClassBuilder {

	public static boolean validClass(Class<?> clazz) {
		try {
			Method serialize = clazz.getDeclaredMethod("serialize");
			if (!serialize.getReturnType().equals(String.class)) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not serializable because it does not return a String");
				return false;
			}
			if (!Modifier.isPublic(serialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not serializable because the method 'serialize' is not public");
				return false;
			}
			if (!Modifier.isStatic(serialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not serializable because the method 'serialize' is static");
				return false;
			}

			Method deserialize = clazz.getDeclaredMethod("deserialize", String.class);
			if (!deserialize.getReturnType().equals(clazz)) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not deserializable because the method 'deserialize' does not return the class '" + clazz.getCanonicalName() + "'");
				return false;
			}

			if (!Modifier.isStatic(deserialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not deserializable because the method 'deserialize' is not static");
				return false;
			}
			if (!Modifier.isPublic(deserialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not deserializable because the method 'deserialize' is not public");
				return false;
			}
		} catch (NoSuchMethodException e) {
			System.out.println("Class '" + clazz.getCanonicalName() + "' is not deserializable because the method 'deserialize' is does not have either the serialize and/or deserialize method(s)");
			return false;
		}
		return true;
	}

	public static Object deserialize(Class<?> clazz, String data) {
		try {
			Method deserialize = clazz.getDeclaredMethod("deserialize", String.class);
			return deserialize.invoke(null, data);
		} catch (NoSuchMethodException e) {
			throw new SerializeException("Could not deserialize data", e.getCause());
		} catch (InvocationTargetException e) {
			throw new SerializeException("Could not deserialize data", e.getCause());
		} catch (IllegalAccessException e) {
			throw new SerializeException("Could not deserialize data", e.getCause());
		}
	}

	public static String serialize(Class<?> clazz, Object object) {
		try {
			Method serialize = clazz.getDeclaredMethod("serialize");
			return (String) serialize.invoke(object);
		} catch (NoSuchMethodException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (InvocationTargetException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (IllegalAccessException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (ClassCastException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		}
	}

}
