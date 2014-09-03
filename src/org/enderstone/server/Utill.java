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
package org.enderstone.server;

import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.play.PacketOutChatMessage;

public class Utill {

	public static void broadcastMessage(String message) {
		PacketOutChatMessage packet = new PacketOutChatMessage(message, false, (byte) 0);
		for (EnderPlayer player : Main.getInstance().onlinePlayers) {
			player.getNetworkManager().sendPacket(packet);
		}
	}

	public static byte calcYaw(float f) {
		int i = (int) f;
		return (byte) (f < i ? i - 1 : i);
	}
}
