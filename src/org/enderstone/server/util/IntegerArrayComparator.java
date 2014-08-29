/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
		System.out.println("TEST: " + xD1 + " " + zD1 + " " + xD2 + " " + zD2 + " " + d1 + " " + d2);
		System.out.println("TEST1: " + fx + " " + fz + " | " + o1[0] + " " + o1[1] + " | " + o2[0] + " " + o2[1]);
		if (d1 > d2) return 1;
		else if (d1 < d2) return -1;
		else if (xD1 < xD2) return -1;
		else if (xD1 > xD2) return 1;
		else if (zD1 < zD2) return -1;
		else if (zD1 > zD2) return 1;
		else return 0;
	}
    
}
