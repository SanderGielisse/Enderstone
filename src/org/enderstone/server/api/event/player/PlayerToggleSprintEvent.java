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

public class PlayerToggleSprintEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final Player player;
	private final boolean sprinting;

	/**
	 * PlayerToggleSprintEvent is called when a player starts or stops sprinting.
	 * 
	 * @param player the player that either started or stopped sprinting
	 * @param sprinting whether or not the player started sprinting
	 */
	public PlayerToggleSprintEvent(Player player, boolean sprinting) {
		this.player = player;
		this.sprinting = sprinting;
	}

	/**
	 * Get the player that either started or stopped sprinting.
	 * 
	 * @return 
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get whether or not the player started sprinting.
	 * 
	 * @return Whether or not the player started sprinting
	 */
	public boolean isSprinting() {
		return sprinting;
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
