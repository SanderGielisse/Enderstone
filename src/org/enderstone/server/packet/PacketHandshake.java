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
package org.enderstone.server.packet;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class PacketHandshake extends Packet {

	private int protocol;
	private String hostname;
	private short port;
	private int nextState;

	public PacketHandshake(int protocol, String hostname, short port, int nextState) {
		this.protocol = protocol;
		this.hostname = hostname;
		this.port = port;
		this.nextState = nextState;
	}

	public PacketHandshake() {
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.protocol = readVarInt(buf);
		this.hostname = readString(buf);
		this.port = buf.readShort();
		this.nextState = readVarInt(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeVarInt(protocol, buf);
		writeString(hostname, buf);
		buf.writeShort(port);
		writeVarInt(nextState, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(protocol) + getStringSize(hostname) + getShortSize() + getVarIntSize(nextState) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	public int getProtocol() {
		return protocol;
	}

	public String getHostname() {
		return hostname;
	}

	public short getPort() {
		return port;
	}

	public int getNextState() {
		return nextState;
	}

	@Override
	public void onRecieve(NetworkManager networkManager) {
		networkManager.latestHandshakePacket = this;
		networkManager.clientVersion = this.protocol;
	}
	
	
}
