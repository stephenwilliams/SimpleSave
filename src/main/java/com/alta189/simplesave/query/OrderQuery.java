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
	private final List<OrderPair> pairs;
	
	public OrderQuery(Query<T> parent) {
		super(QueryType.ORDER);
		this.parent = parent;
		this.pairs = new LinkedList<OrderPair>();
	}
	
	public List<OrderPair> getPairs(){
		return pairs;
	}
	
	@Override
	public QueryResult<T> execute() {
		return parent.execute();
	}
	
	public static class OrderPair {
		
		public final String column;
		public final Order order;
		
		public OrderPair(String column, Order order){
			if (column==null || order==null || column.isEmpty())
				throw new IllegalArgumentException("Invalid OrderPair!");
			this.column = column;
			this.order = order;
		}
		
	}
	
	public static enum Order {
		ASC, DESC;
	}

}
