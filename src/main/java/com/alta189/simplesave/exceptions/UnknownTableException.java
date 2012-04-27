package com.alta189.simplesave.exceptions;

public class UnknownTableException extends RuntimeException {

	public UnknownTableException(String message) {
		super(message);
	}

	public UnknownTableException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownTableException(Throwable cause) {
		super(cause);
	}

}
