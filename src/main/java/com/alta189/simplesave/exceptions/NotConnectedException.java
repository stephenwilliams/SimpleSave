package com.alta189.simplesave.exceptions;

public class NotConnectedException extends RuntimeException {

	public NotConnectedException(String message) {
		super(message);
	}

	public NotConnectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotConnectedException(Throwable cause) {
		super(cause);
	}

}
