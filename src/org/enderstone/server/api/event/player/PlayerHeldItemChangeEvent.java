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

public class PlayerHeldItemChangeEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final Player player;
	private final short oldSlot;
	private final short newSlot;

	/**
	 * PlayerHeldItemChangeEvent is called when a player changes their held item.
	 * 
	 * @param player the player that switched their held item
	 * @param oldSlot the slot that the old item was in
	 * @param newSlot the slot that the new item is in
	 */
	public PlayerHeldItemChangeEvent(Player player, short oldSlot, short newSlot) {
		this.player = player;
		this.oldSlot = oldSlot;
		this.newSlot = newSlot;
	}

	/**
	 * Get the player that switched their held item.
	 * 
	 * @return The player that switched their held item
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the slot that the old item was in.
	 * 
	 * @return The slot that the old item was in.
	 */
	public short getOldSlot() {
		return oldSlot;
	}

	/**
	 * Get the slot that the new item is in.
	 * 
	 * @return The slot that the new item is
	 */
	public short getNewSlot() {
		return newSlot;
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
