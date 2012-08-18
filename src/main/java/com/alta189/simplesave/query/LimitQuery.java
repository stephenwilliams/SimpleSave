package com.alta189.simplesave.query;

public class LimitQuery<T> extends Query<T> {

	private final Query<T> parent;
	private Integer limit;

	public LimitQuery(Query<T> parent) {
		super(QueryType.LIMIT);
		this.parent = parent;
		this.limit = null;
	}
	
	public Integer getLimit(){
		return limit;
	}
	
	/**
	 * Set a limit, or use <code>null</code> to specify no limit.
	 * @param limit Limit to use (cannot be zero or less), or <code>null</code> for none.
	 */
	public void setLimit(Integer limit){
		if (limit!=null && limit<=0)
			throw new RuntimeException("Limit cannot be zero or less!");
		this.limit = limit;
	}

	@Override
	public QueryResult<T> execute() {
		return parent.execute();
	}

}
