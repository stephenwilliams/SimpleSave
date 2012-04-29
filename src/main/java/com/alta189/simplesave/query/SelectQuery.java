package com.alta189.simplesave.query;

import com.alta189.simplesave.Database;

public class SelectQuery<T> extends Query<T> {

	private final Database db;
	private final Class<T> tableClass;
	private final WhereQuery<T> where = new WhereQuery<T>(this);

	public SelectQuery(Database db, Class<T> tableClass) {
		super(QueryType.SELECT);
		this.db = db;
		this.tableClass = tableClass;
	}
	
	public WhereQuery<T> where() {
		return where;
	}

	public Class<T> getTableClass() {
		return tableClass;
	}

	@Override
	public QueryResult<T> execute() {
		return db.execute(this);
	}

}
