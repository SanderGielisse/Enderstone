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

import java.util.Comparator;

/**
 *
 * @author Fernando
 */
public class IntegerArrayComparator implements Comparator<int[]> {
	private final int fx;
	private final int fz;

	public IntegerArrayComparator(int fx, int fz) {
		this.fx = fx;
		this.fz = fz;
	}

	@Override
	public int compare(int[] o1, int[] o2) { // -2,0  0,0
		int xD1 = o1[0] > fx ? o1[0] - fx : fx - o1[0]; // 2
		int zD1 = o1[1] > fz ? o1[1] - fz : fz - o1[1]; // 0
		int xD2 = o2[0] > fx ? o2[0] - fx : fx - o2[0]; // 0
		int zD2 = o2[1] > fz ? o2[1] - fz : fz - o2[1]; // 0
		int d1 = xD1 > zD1 ? xD1 : zD1; // 0
		int d2 = xD2 > zD2 ? xD2 : zD2; // 0
		if (d1 > d2) return 1;
		else if (d1 < d2) return -1;
		else if (xD1 < xD2) return -1;
		else if (xD1 > xD2) return 1;
		else if (zD1 < zD2) return -1;
		else if (zD1 > zD2) return 1;
		else return 0;
	}
    
}
