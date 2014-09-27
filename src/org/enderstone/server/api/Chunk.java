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

public interface Chunk {

	/**
	 * Get the block at the specified location.
	 * 
	 * @param x x position of requested block
	 * @param y y position of requested block
	 * @param z z position of requested block
	 * 
	 * @return The block at the specified location.
	 */
	public Block getBlockAt(int x, int y, int z);

	/**
	 * Get the block at the specified location.
	 * 
	 * @param location the location of the requested block
	 * 
	 * @return The requested block
	 */
	public Block getBlockAt(Location location);

	/**
	 * Get the X position of the chunk.
	 * 
	 * @return The x position of the chunk
	 */
	public int getX();

	/**
	 * Get the Z position of the chunk.
	 * 
	 * @return The Z position of the chunk
	 */
	public int getZ();

	/**
	 * Get the world the chunk is in.
	 * 
	 * @return The world the chunk is in
	 */
	public World getWorld();

	/**
	 * Get the highest block at the specified location.
	 * 
	 * @param x x position of requested block
	 * @param z z position of requested block
	 * 
	 * @return The highest block at the specified location
	 */
	public int getHighestBlockAt(int x, int z);
}
