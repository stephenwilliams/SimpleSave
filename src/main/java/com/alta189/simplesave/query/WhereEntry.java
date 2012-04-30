package com.alta189.simplesave.query;

public class WhereEntry<T> {

	private final Comparator comparator;
	private final String field;
	private final Comparison comparison;
	private final WhereQuery<T> parent;
	private Operator operator;

	public WhereEntry(Comparator comparator, String field, Comparison comparison, WhereQuery<T> parent) {
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

	public Comparison getComparison() {
		return comparison;
	}

	public WhereQuery<T> setOperator(Operator operator) {
		this.operator = operator;
		return parent;
	}

	public WhereQuery<T> and() {
		return setOperator(Operator.AND);
	}

	public WhereQuery<T> or() {
		return setOperator(Operator.OR);
	}

	public QueryResult<T> execute() {
		return parent.execute();
	}
}