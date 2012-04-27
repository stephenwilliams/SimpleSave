package com.alta189.simplesave.query;

import com.alta189.simplesave.Database;

public class SelectQuery extends Query {

	private final Database db;
	private final Class<?> tableClass;
	private final WhereQuery where = new WhereQuery(this);

	public SelectQuery(Database db, Class<?> tableClass) {
		super(QueryType.SELECT);
		this.db = db;
		this.tableClass = tableClass;
	}
	
	public WhereQuery where() {
		return where;
	}

	public Class<?> getTableClass() {
		return tableClass;
	}

	@Override
	public QueryResult execute() {
		return db.execute(this);
	}

}
