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

	/**
	 * Location stores a position and rotation of where an object can be located
	 * 
	 * @param world the world the location is in
	 * @param x the x position of the location
	 * @param y the y position of the location
	 * @param z the z position of the location
	 * @param yaw the yaw of the location
	 * @param pitch the pitch of the location
	 */
	public Location(EnderWorld world, double x, double y, double z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	/**
	 * Get the world the location is in.
	 * 
	 * @return The world the location is in
	 */
	public EnderWorld getWorld() {
		return world;
	}

	/**
	 * Set the world the location is in
	 * 
	 * @param world The new world the location is in
	 */
	public void setWorld(EnderWorld world) {
		this.world = world;
	}

	/**
	 * Get the X position of the location
	 * 
	 * @return The X position of the location
	 */
	public double getX() {
		return x;
	}

	/**
	 * Set the X position of the location
	 * 
	 * @param x The new X position of the location
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Get the Y position of the location
	 * 
	 * @return The Y position of the location
	 */
	public double getY() {
		return y;
	}

	/**
	 * Set the Y position of the location
	 * 
	 * @param y The new Y position of the location
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Get the Z position of the location
	 * 
	 * @return The Z position of the location
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Set the Z position of the location
	 * 
	 * @param z The new Z position of the location
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Get the yaw rotation of the location
	 * 
	 * @return The yaw rotation of the location
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Set the yaw rotation of the location
	 * 
	 * @param yaw The new X yaw rotation of the location
	 */
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	/**
	 * Get the pitch rotation of the location
	 * 
	 * @return The pitch rotation of the location
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Set the pitch rotation of the location
	 * 
	 * @param pitch The new pitch rotation of the location
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * Floors a double (rounds down)
	 * 
	 * @param num The number to be rounded
	 * 
	 * @return Rounded number
	 */
	public static int floor(double num) {
		final int floor = (int) num;
		return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
	}

	/**
	 * Get the floored X position.
	 * 
	 * @return The floored X position
	 */
	public int getBlockX() {
		return floor(getX());
	}

	/**
	 * Get the floored Y position.
	 * 
	 * @return The floored Y position
	 */
	public int getBlockY() {
		return floor(getY());
	}

	/**
	 * Get the floored Z position.
	 * 
	 * @return The floored Z position
	 */
	public int getBlockZ() {
		return floor(getZ());
	}

	/**
	 * Adds the given values to the current location
	 * 
	 * @param x the X value to add
	 * @param y the Y value to add
	 * @param z the Z value to add
	 * 
	 * @return The location after the translation
	 */
	public Location add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Checks if a different location is within range of the current location.
	 * 
	 * @param viewDistance The radius of the range
	 * @param otherLoc The other location to compare with
	 * @param checkY Whether or not to take the Y value into the calculations
	 * 
	 * @return true if the different location is within the range
	 */
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

	/**
	 * Get the distance between a different location and the current location.
	 * 
	 * @param otherLocation the other location to compare with
	 * 
	 * @return The distance between the two locations
	 */
	public double distance(Location otherLocation) {
		double tempX = Math.abs(otherLocation.getX() - this.getX());
		double tempY = Math.abs(otherLocation.getY() - this.getY());
		double tempZ = Math.abs(otherLocation.getZ() - this.getZ());
		return Math.sqrt(tempX * tempX + tempY * tempY + tempZ * tempZ);
	}

	/**
	 * Get the distance between a different location and the current location squared.
	 * This is more efficient than distance.
	 * 
	 * @param otherLocation the other location to compare with
	 * 
	 * @return The distance between the two locations squared
	 */
	public double distanceSquared(Location otherLocation) {
		double tempX = Math.abs(otherLocation.getX() - this.getX());
		double tempY = Math.abs(otherLocation.getY() - this.getY());
		double tempZ = Math.abs(otherLocation.getZ() - this.getZ());
		return tempX * tempX + tempY * tempY + tempZ * tempZ;
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
		return "Location{" + "world=" + world + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + '}';
	}

	/**
	 * Transforms the current location into a different location.
	 * 
	 * @param spawn location to transform into
	 */
	public Location cloneFrom(Location spawn) {
		this.world = spawn.world;
		this.x = spawn.x;
		this.y = spawn.y;
		this.z = spawn.z;
		this.yaw = spawn.yaw;
		this.pitch = spawn.pitch;
		return this;
	}

	/**
	 * Get the chunk's X position the current location is in.
	 * 
	 * @return The chunk's X position the current location is in
	 */
	public int getChunkX() {
		return (getBlockX() >> 4);
	}

	/**
	 * Get the chunk's Z position the current location is in.
	 * 
	 * @return The chunk's Z position the current location is in
	 */
	public int getChunkZ() {
		return (getBlockX() >> 4);
	}

	/**
	 * Applies a vector to the current location.
	 * This method adds the motion of the vector to this location.
	 * 
	 * @param velocity The velocity to apply
	 */
	public void applyVector(Vector velocity) {
		this.x += velocity.motX;
		this.y += velocity.motY;
		this.z += velocity.motZ;
	}
}
