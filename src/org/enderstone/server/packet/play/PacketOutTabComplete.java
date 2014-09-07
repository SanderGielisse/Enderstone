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
import java.util.List;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

/**
 *
 * @author Fernando
 */
public class PacketOutTabComplete extends Packet {

	private int count;
	private List<String> newCommand;

	public PacketOutTabComplete(List<String> newCommand) {
		this.count = newCommand.size();
		this.newCommand = newCommand;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		wrapper.writeVarInt(count);
		for (String l : newCommand)
			wrapper.writeString(l);
	}

	@Override
	public int getSize() throws IOException {
		int size = 0;
		size += getVarIntSize(count);
		for (String l : newCommand)
			size += getStringSize(l);
		return size + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x3A;
	}
}
