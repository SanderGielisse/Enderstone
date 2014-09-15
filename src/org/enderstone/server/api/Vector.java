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

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.motX = x;
		this.motY = y;
		this.motZ = z;
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

	public static Vector substract(Location fromLocation, Location toLocation) {
		return new Vector(toLocation.getX() - fromLocation.getX(), toLocation.getY() - fromLocation.getY(), toLocation.getZ() - fromLocation.getZ());
	}
	
	public Vector normalize(double distance){
		this.x = x / distance;
		this.y = y / distance;
		this.z = z / distance;
		return this;
	}
}
