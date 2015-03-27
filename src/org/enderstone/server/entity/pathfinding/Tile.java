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

public class Tile {

	/**
	 * 
	 * See link below for more information about how this works
	 * http://www.policyalmanac.org/games/aStarTutorial.htm
	 * 
	 */

	private final short x;
	private final short y;
	private final short z;

	private double g = -1;
	private double h = -1;

	private Tile parent = null;

	public Tile(short x, short y, short z, Tile parent) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.parent = parent;
	}

	public boolean isInRange(int range) {
		return ((range - EuclideanUtil.absolute(x) >= 0) && (range - EuclideanUtil.absolute(y) >= 0) && (range - EuclideanUtil.absolute(z) >= 0));
	}

	public void setParent(Tile parent) {
		this.parent = parent;
	}

	public Location getLocation(Location start) {
		return new Location(start.getWorld(), start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z);
	}

	public Tile getParent() {
		return this.parent;
	}

	public short getX() {
		return x;
	}

	public short getY() {
		return y;
	}

	public short getZ() {
		return z;
	}

	public void calculateBoth(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		this.g = EuclideanUtil.calculateG(this, g);
		this.h = EuclideanUtil.calculateH(h, x, y, z, startX, startY, startZ, endX, endY, endZ);
	}

	public double getG() {
		return g;
	}

	public double getH() {
		return h;
	}

	public double getF() {
		return (h + g);
	}

	public String getID() {
		return this.x + "" + this.y + "" + this.z;
	}
}
