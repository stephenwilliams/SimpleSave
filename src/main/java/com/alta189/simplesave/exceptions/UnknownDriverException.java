package com.alta189.simplesave.exceptions;

public class UnknownDriverException extends RuntimeException {

	public UnknownDriverException(String message) {
		super(message);
	}

	public UnknownDriverException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownDriverException(Throwable cause) {
		super(cause);
	}

}
