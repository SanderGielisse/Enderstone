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

public class PlayerChatEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final Player player;
	private String message;
	private String format = "<%name> %message"; // example default

	/**
	 * PlayerChatEvent is called when a player talks in chat.
	 * 
	 * @param player the player that was talking
	 * @param message what was spoken
	 */
	public PlayerChatEvent(Player player, String message) {
		this.player = player;
		this.message = message;
	}

	/**
	 * Get the player that was talking.
	 * 
	 * @return The player that was talking
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the message that was spoken.
	 * 
	 * @return The message that was spoken
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message that will be spoken.
	 * 
	 * @param message the message that will be spoken
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get the format of the current message.
	 * 
	 * @return The format of the current message
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Set the format of the current message.
	 * 
	 * @param format the format of the current message
	 */
	public void setFormat(String format) {
		this.format = format;
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
