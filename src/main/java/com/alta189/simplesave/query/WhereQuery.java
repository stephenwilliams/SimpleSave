package com.alta189.simplesave.query;

import java.util.ArrayList;
import java.util.List;

public class WhereQuery extends Query {

	private final List<WhereEntry> entries = new ArrayList<WhereEntry>();
	private final Query parent;

	public WhereQuery(Query parent) {
		super(QueryType.WHERE);
		this.parent = parent;
	}

	public WhereEntry equal(String field, String comparison) {
		WhereEntry entry = new WhereEntry(Comparator.EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry notEqual(String field, String comparison) {
		WhereEntry entry = new WhereEntry(Comparator.NOT_EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry greaterThan(String field, String comparison) {
		WhereEntry entry = new WhereEntry(Comparator.GREATER_THAN, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry lessThan(String field, String comparison) {
		WhereEntry entry = new WhereEntry(Comparator.LESS_THAN, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry greaterThanOrEqual(String field, String comparison) {
		WhereEntry entry = new WhereEntry(Comparator.GREATER_THAN_OR_EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}

	public WhereEntry lessThanOrEqual(String field, String comparison) {
		WhereEntry entry = new WhereEntry(Comparator.LESS_THAN_OR_EQUAL, field, comparison, this);
		entries.add(entry);
		return entry;
	}
	
	public List<WhereEntry> getEntries() {
		return entries;
	}

	public QueryResult execute() {
		return parent.execute();
	}
}
