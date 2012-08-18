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
