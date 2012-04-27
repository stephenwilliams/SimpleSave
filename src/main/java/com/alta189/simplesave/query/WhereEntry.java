package com.alta189.simplesave.query;

public class WhereEntry {

	private final Comparator comparator;
	private final String field;
	private final String comparison;
	private final WhereQuery parent;
	private Operator operator;

	public WhereEntry(Comparator comparator, String field, String comparison, WhereQuery parent) {
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

	public WhereQuery setOperator(Operator operator) {
		this.operator = operator;
		return parent;
	}

	public QueryResult execute() {
		return parent.execute();
	}
}