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

	public <E> WhereEntry<T> equal(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> notEqual(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.NOT_EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> greaterThan(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.GREATER_THAN, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> lessThan(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.LESS_THAN, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> greaterThanOrEqual(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.GREATER_THAN_OR_EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> lessThanOrEqual(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.LESS_THAN_OR_EQUAL, field, new Comparison<E>(comparison), this);
		entries.add(entry);
		return entry;
	}

	public <E> WhereEntry<T> contains(String field, E comparison) {
		WhereEntry<T> entry = new WhereEntry<T>(Comparator.CONTAINS, field, new Comparison<E>(comparison), this);
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
