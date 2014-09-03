/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.enderstone.server.entity;

import java.util.HashMap;
import java.util.Map;

public class DataWatcher {

	private final Map<Integer, Object> watched = new HashMap<Integer, Object>();

	public void watch(int index, Object value) {
		synchronized (watched) {
			this.watched.put(index, value);
		}
	}

	public Map<Integer, Object> getWatchedCopy() {
		synchronized (watched) {
			return new HashMap<>(watched);
		}
	}
}
