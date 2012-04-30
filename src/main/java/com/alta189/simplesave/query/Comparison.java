package com.alta189.simplesave.query;

public class Comparison<E> {

	private E value;

	public Comparison(E value) {
		this.value = value;
	}

	public Class getType() {
		return value.getClass();
	}

	public E getValue() {
		return value;
	}

}
