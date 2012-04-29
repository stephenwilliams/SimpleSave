package com.alta189.simplesave.query;

import java.util.List;

public class QueryResult<T> {

	private final List<T> results;

	public QueryResult(List<T> results) {
		this.results = results;
	}

	public List<T> find() {
		return results;
	}

	public T findOne() {
		return results.get(0);
	}


}
