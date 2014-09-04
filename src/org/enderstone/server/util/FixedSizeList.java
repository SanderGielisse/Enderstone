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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;

/**
 *
 * @author Fernando
 * @param <E>
 */
public class FixedSizeList<E> extends AbstractList<E> implements RandomAccess, java.io.Serializable {

	private static final long serialVersionUID = -2764017481108945198L;
	private final E[] array;
	private final int start;
	private final int length;

	public FixedSizeList(E[] array) {
		this(array, 0, array.length);
	}

	private FixedSizeList(E[] array, int start, int length) {
		this.array = Objects.requireNonNull(array);
		this.start = start;
		this.length = length;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new FixedSizeList<>(array, start+fromIndex, toIndex - fromIndex);
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public Object[] toArray() {
		return array.clone();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		if (a.length < size)
			return Arrays.copyOf(this.array, size, (Class<? extends T[]>) a.getClass());
		System.arraycopy(this.array, start, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	@Override
	public E get(int index) {
		if(index < 0) throw new IndexOutOfBoundsException("0 <= index < length: "+index);
		if(index >= this.length) throw new IndexOutOfBoundsException("0 <= index < length: "+index);
		return array[start+index];
	}

	@Override
	public E set(int index, E element) {
		if(index < 0) throw new IndexOutOfBoundsException("0 <= index < length: "+index);
		if(index >= this.length) throw new IndexOutOfBoundsException("0 <= index < length: "+index);
		E oldValue = array[start+index];
		array[start+index] = element;
		return oldValue;
	}

	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < length; i++)
				if (array[start+i] == null)
					return i;
		} else {
			for (int i = 0; i < length; i++)
				if (o.equals(array[start+i]))
					return i;
		}
		return -1;
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

//	@Override
//	public Spliterator<E> spliterator() {
//		return Spliterators.spliterator(array, start, length, Spliterator.ORDERED | Spliterator.SIZED);
//	}
}
