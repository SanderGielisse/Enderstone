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

import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;

public interface Block {

	/**
	 * Get the block's X location.
	 * 
	 * @return The block's X location
	 */
	public int getX();

	/**
	 * Get the block's Y location.
	 * 
	 * @return The block's Y location
	 */
	public int getY();

	/**
	 * Get the block's Z location.
	 * 
	 * @return The block's Z location
	 */
	public int getZ();

	/**
	 * Get the world the block is in.
	 * 
	 * @return The world the block is in
	 */
	public EnderWorld getWorld();

	/**
	 * Get the chunk the block is in.
	 * 
	 * @return The chunk the block is in
	 */
	public EnderChunk getChunk();

	/**
	 * Get the block's id.
	 * 
	 * @return The block's id
	 */
	public BlockId getBlock();

	/**
	 * Get the block's data.
	 * 
	 * @return The block's data
	 */
	public byte getData();

	/**
	 * Overwrite the current block at this location.
	 * 
	 * @param id the block id of the new block
	 * @param data the block data of the new block
	 */
	public void setBlock(BlockId id, byte data);

	/**
	 * Get a read only copy of the block.
	 * 
	 * @return A read only copy of the block
	 */
	public Block getReadOnlyCopy();

	/**
	 * Checks whether the block is editable.
	 * 
	 * @return Whether the block is editable
	 */
	public boolean isEditable();
	
	public Location getLocation();

	public Block getRelative(int i, int j, int k);
}
