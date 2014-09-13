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

public class PacketOutPlayerListHeaderFooter extends Packet {

	private String header;
	private String footer;

	private Message messageHeader;
	private Message messageFooter;

	private String jsonHeader;
	private String jsonFooter;

	public PacketOutPlayerListHeaderFooter(String header, String footer) {
		this.header = header;
		this.footer = footer;
	}

	public PacketOutPlayerListHeaderFooter(Message messageHeader, Message messageFooter) {
		this.messageHeader = messageHeader;
		this.messageFooter = messageFooter;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		if (jsonHeader == null) {
			if (messageHeader == null) {
				jsonHeader = new SimpleMessage(header).toMessageJson();
			} else {
				jsonHeader = messageHeader.toMessageJson();
			}
		}
		if (jsonFooter == null) {
			if (messageFooter == null) {
				jsonFooter = new SimpleMessage(footer).toMessageJson();
			} else {
				jsonFooter = messageFooter.toMessageJson();
			}
		}
		wrapper.writeString(jsonHeader);
		wrapper.writeString(jsonFooter);
	}

	@Override
	public int getSize() throws IOException {
		if (jsonHeader == null) {
			if (messageHeader == null) {
				jsonHeader = new SimpleMessage(header).toMessageJson();
			} else {
				jsonHeader = messageHeader.toMessageJson();
			}
		}
		if (jsonFooter == null) {
			if (messageFooter == null) {
				jsonFooter = new SimpleMessage(footer).toMessageJson();
			} else {
				jsonFooter = messageFooter.toMessageJson();
			}
		}
		return getStringSize(jsonFooter) + getStringSize(jsonHeader) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x47;
	}
}
