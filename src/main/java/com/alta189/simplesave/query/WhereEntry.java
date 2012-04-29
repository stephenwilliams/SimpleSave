package com.alta189.simplesave.query;

public class WhereEntry<T> {

	private final Comparator comparator;
	private final String field;
	private final String comparison;
	private final WhereQuery<T> parent;
	private Operator operator;

	public WhereEntry(Comparator comparator, String field, String comparison, WhereQuery<T> parent) {
		this.comparator = comparator;
		this.field = field;
		this.comparison = comparison;
		this.parent = parent;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public String getField() {
		return field;
	}

	public String getComparison() {
		return comparison;
	}

	public WhereQuery<T> setOperator(Operator operator) {
		this.operator = operator;
		return parent;
	}

	public QueryResult<T> execute() {
		return parent.execute();
	}
}