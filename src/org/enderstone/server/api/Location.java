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

import org.enderstone.server.api.entity.Entity;
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
	 * @param world
	 *            the world the location is in
	 * @param x
	 *            the x position of the location
	 * @param y
	 *            the y position of the location
	 * @param z
	 *            the z position of the location
	 * @param yaw
	 *            the yaw of the location
	 * @param pitch
	 *            the pitch of the location
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
	 * Location stores a position and rotation of where an object can be located
	 * 
	 * @param world
	 *            the world the location is in
	 * @param x
	 *            the x position of the location
	 * @param y
	 *            the y position of the location
	 * @param z
	 *            the z position of the location
	 * @param yaw
	 *            the yaw of the location
	 * @param pitch
	 *            the pitch of the location
	 */
	public Location(EnderWorld world, double x, double y, double z, double yaw, double pitch) {
		this(world, (float) x, (float) y, (float) z, (float) yaw, (float) pitch);
	}

	public Location(EnderWorld world, int x, int y, int z) {
		this(world, (float) x, (float) y, (float) z, 0F, 0F);
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
	 * @param world
	 *            The new world the location is in
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
	 * @param x
	 *            The new X position of the location
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
	 * @param y
	 *            The new Y position of the location
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
	 * @param z
	 *            The new Z position of the location
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
	 * @param yaw
	 *            The new X yaw rotation of the location
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
	 * @param pitch
	 *            The new pitch rotation of the location
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * Floors a double (rounds down)
	 * 
	 * @param num
	 *            The number to be rounded
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
	 * @param x
	 *            the X value to add
	 * @param y
	 *            the Y value to add
	 * @param z
	 *            the Z value to add
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
	 * @param viewDistance
	 *            The radius of the range
	 * @param otherLoc
	 *            The other location to compare with
	 * @param checkY
	 *            Whether or not to take the Y value into the calculations
	 * 
	 * @return true if the different location is within the range
	 */
	public boolean isInRange(double viewDistance, Location otherLoc, boolean checkY) {
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
	 * @param otherLocation
	 *            the other location to compare with
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
	 * Get the distance between a different location and the current location squared. This is more efficient than distance.
	 * 
	 * @param otherLocation
	 *            the other location to compare with
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
	 * @param spawn
	 *            location to transform into
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
	 * Applies a vector to the current location. This method adds the motion of the vector to this location.
	 * 
	 * @param velocity
	 *            The velocity to apply
	 * @return the location + the vector
	 */
	public Location applyVector(Vector vector) {
		this.x += vector.motX;
		this.y += vector.motY;
		this.z += vector.motZ;
		return this;
	}

	public static double calcYaw(Location fromLocation, Location toLocation) {
		double x0 = fromLocation.getX();
		double z0 = fromLocation.getZ();
		double x = toLocation.getX();
		double z = toLocation.getZ();
		double l = x - x0;
		double w = z - z0;
		double c = Math.sqrt(l * l + w * w);
		double alpha1 = -Math.asin((l / c)) / Math.PI * 180D;
		double alpha2 = Math.acos((w / c)) / Math.PI * 180D;
		double yaw;
		if (alpha2 > 90) {
			yaw = 180 - alpha1;
		} else {
			yaw = alpha1;
		}
		return yaw;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(pitch);
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Float.floatToIntBits(yaw);
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (Float.floatToIntBits(pitch) != Float.floatToIntBits(other.pitch))
			return false;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Float.floatToIntBits(yaw) != Float.floatToIntBits(other.yaw))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	public Block getBlock() {
		return this.world.getBlock(this.getBlockX(), this.getBlockY(), this.getBlockZ());
	}

	public Entity getNearestEntity(int range) {
		for(Entity e : this.world.getEntities()){
			if(e.getLocation().distanceSquared(this) <= range * range){
				return e;
			}
		}
		return null;
	}
}
