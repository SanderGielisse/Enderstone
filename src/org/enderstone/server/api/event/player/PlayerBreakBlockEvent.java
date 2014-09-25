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

import org.enderstone.server.api.Block;
import org.enderstone.server.api.event.Cancellable;
import org.enderstone.server.api.event.Event;
import org.enderstone.server.entity.EnderPlayer;

public class PlayerBreakBlockEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final EnderPlayer player;
	private final Block block;

	/**
	 * PlayerBreakBlockEvent is called when a player breaks a block.
	 * 
	 * @param player player that broke the block
	 * @param block the block that was broken
	 */
	public PlayerBreakBlockEvent(EnderPlayer player, Block block) {
		this.player = player;
		this.block = block;
	}

	/**
	 * Get the player that broke the block.
	 * 
	 * @return The player that broke the block
	 */
	public EnderPlayer getPlayer() {
		return player;
	}

	/**
	 * Get the block that was broken.
	 * 
	 * @return The block that was broken
	 */
	public Block getBlock() {
		return block;
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
