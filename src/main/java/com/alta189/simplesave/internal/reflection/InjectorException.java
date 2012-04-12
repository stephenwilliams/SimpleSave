package com.alta189.simplesave.internal.reflection;

public class InjectorException extends RuntimeException {

	public InjectorException(String message) {
		super(message);
	}

	public InjectorException(String message, Throwable cause) {
		super(message, cause);
	}

	public InjectorException(Throwable cause) {
		super(cause);
	}
}
