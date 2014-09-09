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

	public Block getBlock(int x, int y, int z);

	public Block getBlock(Location location);

	public Chunk getChunkAt(int x, int z);

	public Collection<? extends Chunk> getLoadedChunks();

	public Item dropItem(Location location, ItemStack itemStack, int pickupDelay);

	public void strikeLightning(Location location);

	public Collection<? extends Entity> getEntities();

	public Collection<? extends Player> getPlayers();

	public Collection<? extends Mob> getMobs();

	public String getName();

	public long getSeed();

	public void playSound(Location location, String soundName, float volume, int pitch);
}
