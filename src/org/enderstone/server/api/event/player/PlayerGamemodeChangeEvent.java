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

import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.api.event.Cancellable;
import org.enderstone.server.api.event.Event;

public class PlayerGamemodeChangeEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final Player player;
	private final GameMode oldGamemode;
	private final GameMode newGamemode;

	/**
	 * PlayerGamemodeChangeEvent is called when a player's gamemode changes.
	 * 
	 * @param player the player whose gamemode was changed
	 * @param oldGamemode the previous gamemode the player was in
	 * @param newGamemode the current gamemode the player is in
	 */
	public PlayerGamemodeChangeEvent(Player player, GameMode oldGamemode, GameMode newGamemode) {
		this.player = player;
		this.oldGamemode = oldGamemode;
		this.newGamemode = newGamemode;
	}

	/**
	 * Get the player whose gamemode was changed.
	 * 
	 * @return The player whose gamemode was changed
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the previous gamemode the player was in.
	 * 
	 * @return The previous gamemode the player was in
	 */
	public GameMode getOldGamemode() {
		return oldGamemode;
	}

	/**
	 * Get the current gamemode the player is in.
	 * 
	 * @return The current gamemode that player is in.
	 */
	public GameMode getNewGamemode() {
		return newGamemode;
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
