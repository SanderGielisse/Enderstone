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
import org.enderstone.server.api.event.Event;
import org.enderstone.server.api.messages.Message;

public class PlayerJoinEvent extends Event {

	private Message disconnectMessage = null;
	private final Player player;

	/**
	 * PlayerJoinEvent is called when a player joins a server.
	 * 
	 * @param player the player that joined the server
	 */
	public PlayerJoinEvent(Player player) {
		this.player = player;
	}

	/**
	 * Get the player that joined the server.
	 * 
	 * @return The player that joined the server
	 */
	public Player getPlayer() {
		return player;
	}

	public Message getDisconnectMessage() {
		return disconnectMessage;
	}

	public void setDisconnectMessage(Message disconnectMessage) {
		this.disconnectMessage = disconnectMessage;
	}
}
