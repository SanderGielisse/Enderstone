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

public class PlayerCommandEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final Player player;
	private final String command;
	private final String[] arguments;

	/**
	 * PlayerCommandEvent is called when a player uses a command.
	 * 
	 * @param player the player that used the command
	 * @param command the command that was used
	 * @param arguments the arguments of the command
	 */
	public PlayerCommandEvent(Player player, String command, String[] arguments) {
		this.player = player;
		this.command = command;
		this.arguments = arguments;
	}

	/**
	 * Get the player that used the command.
	 * 
	 * @return The player that used the command
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the command that was used.
	 * 
	 * @return The command that was used
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Get the arguments of the command.
	 * 
	 * @return The arguments of the command
	 */
	public String[] getArguments() {
		return arguments;
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
