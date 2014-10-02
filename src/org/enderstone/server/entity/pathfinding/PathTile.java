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

import org.enderstone.server.api.Location;

/**
 *
 * @author gyroninja
 */
public class PathTile {

	private final int offsetX;
	private final int offsetY;
	private final int offsetZ;

	private double goalCost = -1;
	private double heuristicCost = -1;

	private final int startX;
	private final int startY;
	private final int startZ;

	private final int endX;
	private final int endY;
	private final int endZ;

	private PathTile parent = null;

	public PathTile(int x, int y, int z, PathTile parent, int startX, int startY, int startZ, int endX, int endY, int endZ) {

		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;

		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;

		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;

		this.parent = parent;

		calculateCost();
	}

	public int getXOffset() {

		return offsetX;
	}

	public int getYOffset() {

		return offsetY;
	}

	public int getZOffset() {

		return offsetZ;
	}

	public double getGoalCost() {

		return goalCost;
	}

	public double getHeuristicCost() {

		return heuristicCost;
	}

	public PathTile getParent() {

		return parent;
	}

	public void setParent(PathTile parent) {

		this.parent = parent;

		calculateGoalCost();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}

		if (!(obj instanceof PathTile)) {

			return false;
		}

		PathTile tile = (PathTile) obj;

		return tile.getXOffset() == getXOffset() && tile.getYOffset() == getYOffset() && tile.getZOffset() == getZOffset();
	}

	@Override
	public int hashCode() {

		return calculateHashCode(offsetX, offsetY, offsetZ);
	}

	public static int calculateHashCode(int x, int y, int z) {

		int hash = 7;

		hash = 89 * hash + x;
		hash = 89 * hash + y;
		hash = 89 * hash + z;

		return hash;
	}

	@Override
	public String toString() {

		return "PathTile{" + "xOffset=" + offsetX + " yOffset=" + offsetY + " zOffset=" + offsetZ + '}';
	}

	public boolean isInRange(int range) {

		return (range - Math.abs(offsetX) >= 0 && range - Math.abs(offsetY) >= 0 && range - Math.abs(offsetZ) >= 0);
	}

	public double getDistance(int startX, int startY, int startZ, int endX, int endY, int endZ) {

		int dx = startX - endX;
		int dy = startY - endY;
		int dz = startZ - endZ;

		return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}

	public Location getLocation(Location start) {

		return start.clone().add(offsetX, offsetY, offsetZ);
	}

	private void calculateCost() {

		calculateGoalCost();
		calculateHeuristicCost();
	}

	//TODO Add cost to go through water and other blocks
	private void calculateGoalCost() {

		if (goalCost == -1) {

			PathTile currentTile = this;
			PathTile currentParent;

			int pCost = 0;

			while ((currentParent = currentTile.getParent()) != null) {

				int dx = Math.abs(currentTile.getXOffset() - currentParent.getXOffset());
				int dy = Math.abs(currentTile.getYOffset() - currentParent.getYOffset());
				int dz = Math.abs(currentTile.getZOffset() - currentParent.getZOffset());

				if (dx == 1 && dy == 1 && dz == 1) {

					pCost += 1.7;//Square root of 3 (length of bottom left of cube to top right of cube)
				}

				else if (((dx == 1 || dz == 1) && dy == 1) || ((dx == 1 || dz == 1) && dy == 0)) {

					pCost += 1.4;//Square root of 2 (length of bottom left of square to top right of square)
				}

				else {

					pCost += 1;//Square root of 1
				}

				currentTile = currentParent;
			}

			this.goalCost = pCost;
		}
	}

	private void calculateHeuristicCost() {

		if (heuristicCost == -1) {

			int futureX = startX + offsetX;
			int futureY = startY + offsetY;
			int futureZ = startZ + offsetZ;

			this.heuristicCost = getDistance(futureX, futureY, futureZ, endX, endY, endZ);
		}
	}
}
