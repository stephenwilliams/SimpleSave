package com.alta189.simplesave.query;

public abstract class Query {

	private final QueryType type;
	
	public Query(QueryType type) {
		this.type = type;
	}

	public QueryType getType() {
		return type;
	}

	public abstract QueryResult execute();

}
