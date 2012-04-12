package com.alta189.simplesave.internal.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleInjector implements Injector {

	private Object[] args;
	private Class<?>[] argClasses;

	public SimpleInjector(Object... args) {
		this.args = args;
		argClasses = new Class[args.length];
		for (int i = 0; i < args.length; ++i) {
			argClasses[i] = args[i].getClass();
		}
	}

	public Object newInstance(Class<?> clazz) {
		try {
			Constructor<?> ctr = clazz.getConstructor(argClasses);
			return ctr.newInstance(args);
		} catch (NoSuchMethodException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (InvocationTargetException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (InstantiationException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (IllegalAccessException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		}
	}

}
