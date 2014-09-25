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

public class PlayerExpChangeEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final Player player;
	private final float oldExp;
	private final float newExp;

	/**
	 * PlayerExpChangeEvent is called when a player's exp amount changes.
	 * 
	 * @param player the player whose exp changed
	 * @param oldExp the old amount of exp
	 * @param newExp the new amount of exp
	 */
	public PlayerExpChangeEvent(Player player, float oldExp, float newExp) {
		this.player = player;
		this.oldExp = oldExp;
		this.newExp = newExp;
	}

	/**
	 * Get the player whose exp changed.
	 * 
	 * @return The player whose exp changed
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the old amount of exp.
	 * 
	 * @return The old amount of exp
	 */
	public float getOldExp() {
		return oldExp;
	}

	/**
	 * Get the new amount of exp.
	 * 
	 * @return The new amount of exp
	 */
	public float getNewExp() {
		return newExp;
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
