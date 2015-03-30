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

package org.enderstone.server.entity.pathfinding;

public class EuclideanUtil {

	/**
	 * 
	 * See link below for more information about how this works
	 * http://www.policyalmanac.org/games/aStarTutorial.htm
	 * 
	 * Thanks to @Adamki11s, who's source I took inspiration from on how to do this, check out his Bukkit post here:
	 * https://bukkit.org/threads/lib-a-pathfinding-algorithm.129786/
	 * 
	 */

	public static double euclideanDistance(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		double dx = startX - endX;
		double dy = startY - endY;
		double dz = startZ - endZ;
		return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}

	public static double calculateH(double h, int x, int y, int z, int startX, int startY, int startZ, int endX, int endY, int endZ) {
		return euclideanDistance(startX + x, startY + y, startZ + z, endX, endY, endZ);
	}

	// G = the movement cost to move from the starting point A to a given square on the grid, following the path generated to get there
	public static double calculateG(Tile currentTile, double g) {
		Tile currentParent = currentTile.getParent();
		int newG = 0;
		// follow path back to start
		while ((currentParent = currentTile.getParent()) != null) {

			int dx = absolute(currentTile.getX() - currentParent.getX());
			int dy = absolute(currentTile.getY() - currentParent.getY());
			int dz = absolute(currentTile.getZ() - currentParent.getZ());

			if (dx == 1 && dy == 1 && dz == 1) {
				newG += 1.7;
			} else if (((dx == 1 || dz == 1) && dy == 1) || ((dx == 1 || dz == 1) && dy == 0)) {
				newG += 1.4;
			} else {
				newG += 1.0;
			}

			// go back one tile
			currentTile = currentParent;
		}
		return newG;
	}

	public static int absolute(int i) {
		return (i < 0 ? -i : i);
	}
}
