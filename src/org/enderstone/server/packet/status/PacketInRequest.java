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
package org.enderstone.server.packet.status;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.json.JSONObject;

public class PacketInRequest extends Packet {

	// no fields

	@Override
	public void read(ByteBuf buf) throws IOException {
		// none
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	@Override
	public void onRecieve(NetworkManager networkManager) {
		int protocol = networkManager.latestHandshakePacket.getProtocol();
		if (!Main.PROTOCOL.contains(protocol)) {
			protocol = Main.DEFAULT_PROTOCOL;
		}

		JSONObject json = new JSONObject();
		json.put("version", new JSONObject().put("name", Main.PROTOCOL_VERSION).put("protocol", protocol));
		json.put("players", new JSONObject().put("max", 20).put("online", Main.getInstance().onlinePlayers.size()));
		json.put("description", Main.getInstance().prop.get("motd"));

		if (Main.getInstance().FAVICON != null) {
			json.put("favicon", Main.getInstance().FAVICON);
		}

		networkManager.sendPacket(new PacketOutResponse(json.toString()));
	}
}
