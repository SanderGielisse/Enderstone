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

import java.util.Collection;
import org.enderstone.server.api.entity.Entity;
import org.enderstone.server.api.entity.Item;
import org.enderstone.server.api.entity.Mob;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.inventory.ItemStack;

public interface World {

	/**
	 * Gets the block at the requested location.
	 * 
	 * @param x The X position of the requested block
	 * @param y The Y position of the requested block
	 * @param z The Z position of the requested block
	 * 
	 * @return The requested block
	 */
	public Block getBlock(int x, int y, int z);

	/**
	 * Gets the block at the requested location.
	 * 
	 * @param location The location of the requested block.
	 * 
	 * @return The requested block
	 */
	public Block getBlock(Location location);

	/**
	 * Gets the chunk at the requested location.
	 * 
	 * @param x The X position of the requested chunk
	 * @param z The Z position of the requested chunk
	 * 
	 * @return The requested chunk
	 */
	public Chunk getChunkAt(int x, int z);

	/**
	 * Gets the loaded chunks in the world.
	 * 
	 * @return A collection of the loaded chunks.
	 */
	public Collection<? extends Chunk> getLoadedChunks();

	/**
	 * Drops an item in the world.
	 * 
	 * @param location the location to drop the item
	 * @param itemStack the item to drop
	 * @param pickupDelay the pickup delay of the dropped item
	 * 
	 * @return The dropped item entity
	 */
	public Item dropItem(Location location, ItemStack itemStack, int pickupDelay);

	/**
	 * Strikes lighting at a location.
	 * 
	 * @param location The location to strike the lightning
	 */
	public void strikeLightning(Location location);

	/**
	 * Gets the loaded entities in the world.
	 * 
	 * @return A collection of the loaded entities
	 */
	public Collection<? extends Entity> getEntities();

	/**
	 * Gets the current players in the world.
	 * 
	 * @return A collection of the players in a world
	 */
	public Collection<? extends Player> getPlayers();

	/**
	 * Gets the loaded mobs in the world.
	 * 
	 * @return A collection of the loaded mobs
	 */
	public Collection<? extends Mob> getMobs();

	/**
	 * Gets the name of the world.
	 * 
	 * @return The name of the world
	 */
	public String getName();

	/**
	 * Gets the seed of the world.
	 * 
	 * @return The seed of the world
	 */
	public long getSeed();

	/**
	 * Plays a sound in the world.
	 * 
	 * @param location The location to play the sound
	 * @param soundName The sound to be played
	 * @param volume the volume to play the sound at
	 * @param pitch the pitch to play the sound at
	 */
	public void playSound(Location location, String soundName, float volume, int pitch);
}
