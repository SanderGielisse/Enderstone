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
package org.enderstone.server.api.event.player;

import org.enderstone.server.api.entity.Player;
import org.enderstone.server.api.event.Cancellable;
import org.enderstone.server.api.event.Event;
import org.enderstone.server.inventory.ItemStack;

public class PlayerDropItemEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final Player player;
	private final ItemStack droppedItem;

	/**
	 * PlayerDropItemEvent is called when a player drops an item from 
	 * <ul>
	 * <li>Pressing the drop key</li>
	 * <li>Dropping from inside an inventory</li>
	 * </ul>
	 * 
	 * @param player the player that dropped the item
	 * @param item the item that was dropped
	 */
	public PlayerDropItemEvent(Player player, ItemStack droppedItem) {
		this.player = player;
		this.droppedItem = droppedItem;
	}

	/**
	 * Get the player that dropped the item.
	 * 
	 * @return The player that dropped the item
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the item that was dropped.
	 * 
	 * @return The item that was dropped
	 */
	public ItemStack getDroppedItem() {
		return droppedItem;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
