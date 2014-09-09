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
package org.enderstone.server.api;

import org.enderstone.server.regions.EnderWorld;

public class Location implements Cloneable {

	private EnderWorld world;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	
	public Location(EnderWorld world, double x, double y, double z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public EnderWorld getWorld() {
		return world;
	}

	public void setWorld(EnderWorld world) {
		this.world = world;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public static int floor(double num) {
		final int floor = (int) num;
		return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
	}

	public int getBlockX() {
		return floor(getX());
	}

	public int getBlockZ() {
		return floor(getZ());
	}

	public int getBlockY() {
		return floor(getY());
	}

	public Location add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public boolean isInRange(int viewDistance, Location otherLoc, boolean checkY) {
		if (this.world != null && otherLoc.world != null) {
			if (!this.world.worldName.equals(otherLoc.world.worldName)) {
				return false;
			}
		}
		if (!checkY) {
			return Math.max(this.x < otherLoc.x ? otherLoc.x - this.x : this.x - otherLoc.x, this.z < otherLoc.z ? otherLoc.z - this.z : this.z - otherLoc.z) < viewDistance;
		} else {
			return Math.max(this.x < otherLoc.x ? otherLoc.x - this.x : this.x - otherLoc.x, Math.max(this.z < otherLoc.z ? otherLoc.z - this.z : this.z - otherLoc.z, this.y < otherLoc.y ? otherLoc.y - this.y : this.y - otherLoc.y)) < viewDistance;
		}
	}

	@Override
	public Location clone() {
		try {
			return (Location) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		return "Location{" + "world=" + world.toString() + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + '}';
	}

	public void cloneFrom(Location spawn) {
		this.world = spawn.world;
		this.x = spawn.x;
		this.y = spawn.y;
		this.z = spawn.z;
		this.yaw = spawn.yaw;
		this.pitch = spawn.pitch;
	}
}
