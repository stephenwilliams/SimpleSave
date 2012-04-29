package com.alta189.simplesave.query;

import java.util.ArrayList;
import java.util.List;

public class WhereQuery<T> extends Query<T> {

	private final List<WhereEntry<T>> entries;
	private final Query<T> parent;

	public WhereQuery(Query<T> parent) {
		super(QueryType.WHERE);
		this.parent = parent;
		entries = new ArrayList<WhereEntry<T>>();
	}

	public WhereEntry<T> equal(String field, String comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry<T> notEqual(String field, String comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.NOT_EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry<T> greaterThan(String field, String comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.GREATER_THAN, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry<T> lessThan(String field, String comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.LESS_THAN, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry<T> greaterThanOrEqual(String field, String comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.GREATER_THAN_OR_EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry<T> lessThanOrEqual(String field, String comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.LESS_THAN_OR_EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}
	
	public List<WhereEntry<T>> getEntries() {
		return entries;
	}

	public QueryResult<T> execute() {
		return parent.execute();
	}
}
