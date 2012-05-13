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
package com.alta189.simplesave.internal;

public class FieldRegistration {
	private final String name;
	private final Class<?> type;
	private final boolean serializable;

	public FieldRegistration(String name, Class<?> type) {
		this(name, type, false);
	}

	public FieldRegistration(String name, Class<?> type, boolean serializable) {
		this.name = name;
		this.type = type;
		this.serializable = serializable;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isSerializable() {
		return serializable;
	}
}
