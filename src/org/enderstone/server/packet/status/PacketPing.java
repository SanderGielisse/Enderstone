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
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketPing extends Packet {

	private long time;

	public PacketPing() {
	}

	public PacketPing(long time) {
		this.time = time;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		time = buf.readLong();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeLong(time);
	}

	@Override
	public int getSize() throws IOException {
		return getLongSize() + getVarIntSize(getId());
	}

	@Override
	public void onRecieve(NetworkManager networkManager) {
		networkManager.sendPacket(this);
	}

	@Override
	public byte getId() {
		return 0x01;
	}
}
