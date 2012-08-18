package com.alta189.simplesave.query;

import java.util.LinkedList;
import java.util.List;

public class OrderQuery<T> extends Query<T> {

	private final Query<T> parent;
	private final List<String> columnnames;
	private Order order;
	
	public OrderQuery(Query<T> parent) {
		super(QueryType.ORDER);
		this.parent = parent;
		this.columnnames = new LinkedList<String>();
		this.order = Order.ASC;
	}
	
	public List<String> getColumnNames(){
		return columnnames;
	}
	
	public Order getOrder(){
		return order;
	}
	
	public void setOrder(Order order){
		this.order = order;
	}

	@Override
	public QueryResult<T> execute() {
		return parent.execute();
	}
	
	public static enum Order {
		ASC, DESC;
	}

}
