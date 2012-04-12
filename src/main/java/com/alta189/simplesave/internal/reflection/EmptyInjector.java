package com.alta189.simplesave.internal.reflection;

public class EmptyInjector implements Injector {

	@Override
	public Object newInstance(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (IllegalAccessException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		}
	}

}
