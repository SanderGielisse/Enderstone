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
package org.enderstone.server.packet.play;

import java.io.IOException;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketOutChatMessage extends Packet {

	private Message message;
	private String jsonChat;
	private byte position;

	public PacketOutChatMessage(String chatMessage, boolean json, byte position) {
		if (json) {
			this.jsonChat = chatMessage;
		} else {
			this.message = new SimpleMessage(chatMessage);
		}
		this.position = position;
	}
	
	public PacketOutChatMessage(Message message, byte position)
	{
		this.message = message;
		this.position = position;
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		if(this.jsonChat == null) this.jsonChat = message.toMessageJson();
		if (getStringSize(jsonChat) > 32767) {
			throw new IllegalArgumentException("The chat messages can't be any longer than 32767 bytes!");
		}
		wrapper.writeString(this.jsonChat);
		wrapper.writeByte(position);
	}

	@Override
	public int getSize() throws IOException {
		if(this.jsonChat == null) this.jsonChat = message.toMessageJson();
		return getStringSize(this.jsonChat) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x02;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}
}
