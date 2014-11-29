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

public class Vector {

	private double x;
	private double y;
	private double z;

	public double motX;
	public double motY;
	public double motZ;

	/**
	 * Creates a new vector with the X, Y, and Z values assigned to both it's location and velocity.
	 * 
	 * @param x
	 *            the location and velocity of the vector along the X axis
	 * @param y
	 *            the location and velocity of the vector along the Y axis
	 * @param z
	 *            the location and velocity of the vector along the Z axis
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.motX = x;
		this.motY = y;
		this.motZ = z;
	}

	/**
	 * Get the X location of the vector.
	 * 
	 * @return The X location of the vector
	 */
	public double getX() {
		return x;
	}

	/**
	 * Set the X location of the vector.
	 * 
	 * @param x
	 *            the new X location of the vector
	 */
	public Vector setX(double x) {
		this.x = x;
		return this;
	}

	/**
	 * Get the Y location of the vector.
	 * 
	 * @return The Y location of the vector
	 */
	public double getY() {
		return y;
	}

	/**
	 * Set the Y location of the vector.
	 * 
	 * @param y
	 *            the new Y location of the vector
	 */
	public Vector setY(double y) {
		this.y = y;
		return this;
	}

	/**
	 * Get the Z location of the vector.
	 * 
	 * @return The Z location of the vector
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Set the Z location of the vector.
	 * 
	 * @param z
	 *            the new Z location of the vector
	 */
	public Vector setZ(double z) {
		this.z = z;
		return this;
	}

	/**
	 * Gets the vector of the difference of two locations.
	 * 
	 * @param fromLocation
	 *            starting location
	 * @param toLocation
	 *            ending location
	 * 
	 * @return The difference of two locations
	 */
	public static Vector substract(Location fromLocation, Location toLocation) {
		return new Vector(toLocation.getX() - fromLocation.getX(), toLocation.getY() - fromLocation.getY(), toLocation.getZ() - fromLocation.getZ());
	}

	public static Vector substractAndNormalize(Location fromLocation, Location toLocation) {
		Vector vec = substract(fromLocation, toLocation);
		vec.normalize(fromLocation.distance(toLocation));
		return vec;
	}

	/**
	 * Normalize the vector and also returns itself.
	 * 
	 * @param distance
	 *            the distance to normalize
	 * 
	 * @return The normalized vector
	 */
	public Vector normalize(double distance) {
		this.x = x / distance;
		this.y = y / distance;
		this.z = z / distance;
		return this;
	}

	public Vector multiply(float f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
		return this;
	}

	public Vector add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public String toString() {
		return "Vector [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	public Vector add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
}
