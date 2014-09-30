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
import org.enderstone.server.regions.EnderChunk;

/**
 *
 * @author gyroninja
 */
public class PathTile {

	private final int xOffset;
	private final int yOffset;
	private final int zOffset;

	private double goalCost = -1;
	private double heuristicCost = -1;

	private PathTile parent = null;

	public PathTile(int x, int y, int z, PathTile parent) {
		this.xOffset = x;
		this.yOffset = y;
		this.zOffset = z;

		this.parent = parent;
	}

	public int getXOffset() {
		return xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}

	public int getZOffset() {
		return zOffset;
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
		int hash = 7;
		hash = 89 * hash + this.xOffset;
		hash = 89 * hash + this.yOffset;
		hash = 89 * hash + this.zOffset;
		return hash;
	}

	@Override
	public String toString() {
		return "PathTile{" + "xOffset=" + xOffset + " yOffset=" + yOffset + " zOffset=" + zOffset + '}';
	}

	public boolean isInRange(int range) {
		return (range - Math.abs(xOffset) >= 0 && range - Math.abs(yOffset) >= 0 && range - Math.abs(zOffset) >= 0);
	}

	public double getDistance(int startX, int startY, int startZ, int endX, int endY, int endZ) {

		int dx = startX - endX;
		int dy = startY - endY;
		int dz = startZ - endZ;

		return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}

	public Location getLocation(Location start) {

		return start.clone().add(xOffset, yOffset, zOffset);
	}

	public void calculateCost(int startX, int startY, int startZ, int endX, int endY, int endZ) {

		calculateGoalCost(xOffset, xOffset, xOffset);
		calculateHeuristicCost(xOffset, xOffset, xOffset, xOffset, xOffset, xOffset);
	}

	private void calculateGoalCost(int startX, int startY, int startZ) {

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

	private void calculateHeuristicCost(int startX, int startY, int startZ, int endX, int endY, int endZ) {

		if (heuristicCost == -1) {

			int futureX = startX + xOffset;
			int futureY = startY + yOffset;
			int futureZ = startZ + zOffset;

			this.heuristicCost = getDistance(futureX, futureY, futureZ, endX, endY, endZ);
		}
	}
}
