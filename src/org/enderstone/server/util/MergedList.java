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

package org.enderstone.server.util;

import java.util.AbstractList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.RandomAccess;

/**
 *
 * @author ferrybig
 * @param <E>
 */
public class MergedList<E> extends AbstractList<E> implements RandomAccess, java.io.Serializable {

	private final List<Entry<List<E>,Integer>> indexes;

	private  MergedList(List<Entry<List<E>,Integer>> indexes) {
		this.indexes = indexes;
	}
	
	@Override
	public E get(int index) {
		Entry<List<E>,Integer> entry  = this.indexes.get(index);
		return entry.getKey().get(entry.getValue());
	}

	@Override
	public E set(int index, E element) {
		Entry<List<E>,Integer> entry  = this.indexes.get(index);
		return entry.getKey().set(entry.getValue(),element);
	}

	@Override
	public int size() {
		return indexes.size();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + Objects.hashCode(this.indexes);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final MergedList<?> other = (MergedList<?>) obj;
		return Objects.equals(this.indexes, other.indexes);
	}
	
	public static class Builder<E> {
		
		private Entry<List<E>,Integer>[] indexes = new Entry[16];
		private int higestIndex;
		
		public Builder<E> addList(int offset, List<E> list)
		{
			return this.addList(offset,list,0,list.size());
		}
		
		public Builder<E> addList(int offset, final List<E> list, int startIndex, int mergeLength)
		{
			if(this.indexes.length < offset+mergeLength)
			{
				Entry<List<E>,Integer>[] a = new Entry[offset+mergeLength+16];
				System.arraycopy(indexes, 0, a, 0, higestIndex);
				indexes = a;
			}
			if(higestIndex < offset+mergeLength)
			{
				higestIndex = offset+mergeLength;
			}
			for(int i = 0; i < mergeLength; i++)
			{
				final int index = i + startIndex;
				this.indexes[i + offset] = new Entry<List<E>, Integer>(){

					@Override
					public List<E> getKey() {
						return list;
					}

					@Override
					public Integer getValue() {
						return index;
					}

					@Override
					public Integer setValue(Integer value) {
						throw new UnsupportedOperationException("Not supported yet.");
					}
				};
			}
			return this;
		}
		public MergedList build()
		{
			return new MergedList(new FixedSizeList<>(indexes,0,higestIndex));
		}
	}
}
