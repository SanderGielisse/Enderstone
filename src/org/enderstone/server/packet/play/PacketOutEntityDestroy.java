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
import java.util.Arrays;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketOutEntityDestroy extends Packet {

	private int length;
	private Integer[] ids;

	public PacketOutEntityDestroy(Integer[] ids) {
		this.length = ids.length;
		this.ids = ids;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		wrapper.writeVarInt(length);
		for (int i = 0; i < length; i++) {
			wrapper.writeVarInt(this.ids[i]);
		}
	}

	@Override
	public int getSize() throws IOException {
		int size = 0;
		size += getVarIntSize(length);
		for (int i = 0; i < length; i++) {
			size += getVarIntSize(ids[i]);
		}
		return size + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x13;
	}

	@Override
	public String toString() {
		return "PacketOutEntityDestroy [length=" + length + ", ids=" + Arrays.toString(ids) + "]";
	}
}
