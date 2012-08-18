/*
 * This file is part of SimpleSave
 *
 * SimpleSave is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleSave is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
