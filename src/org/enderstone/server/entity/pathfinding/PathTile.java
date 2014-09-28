/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.entity.pathfinding;

import org.enderstone.server.api.Location;

/**
 *
 * @author gyroninja
 */
public class PathTile {

	private final int xOffset;
	private final int yOffset;
	private final int zOffset;

	private double pastCost = -1;
	private double futureCost = -1;

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

	public double getPastCost() {
		return pastCost;
	}

	public double getFutureCost() {
		return futureCost;
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
		return (range - abs(xOffset) >= 0 && range - abs(yOffset) >= 0 && range - abs(zOffset) >= 0);
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

	public void calculatePastCost(int startX, int startY, int startZ, boolean update) {

		if (update ^ pastCost == -1) {

			PathTile currentTile = this;
			PathTile currentParent;

			int pCost = 0;

			while ((currentParent = currentTile.getParent()) != null) {

				int dx = abs(currentTile.getXOffset() - currentParent.getXOffset());
				int dy = abs(currentTile.getYOffset() - currentParent.getYOffset());
				int dz = abs(currentTile.getZOffset() - currentParent.getZOffset());

				if (dx == 1 && dy == 1 && dz == 1) {

					pCost += 1.7;//Square root of 3
				}

				else if ((dx == 1 || dz == 1) && (dy == 1 || dy == 0)) {

					pCost += 1.4;//Square root of 2
				}

				else {

					pCost += 1;//Square root of 1
				}

				currentTile = currentParent;
			}

			this.pastCost = pCost;
		}
	}

	public void calculateFutureCost(int startX, int startY, int startZ, int endX, int endY, int endZ, boolean update) {

		if (update ^ pastCost == -1) {

			int futureX = startX + xOffset;
			int futureY = startY + yOffset;
			int futureZ = startZ + zOffset;

			this.futureCost = getDistance(futureX, futureY, futureZ, endX, endY, endZ);
		}
	}

	private int abs(int x) {

		return x > 0 ? x : -x;
	}
}
