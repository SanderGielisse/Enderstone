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
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketOutSpawnObject extends Packet {

	private int entityId;
	private byte entityType;
	private int x;
	private int y;
	private int z;
	private byte pitch;
	private byte yaw;

	private int dataSize;
	private short dataOne;
	private short dataTwo;
	private short dataThree;

	public PacketOutSpawnObject(int entityId, byte entityType, int x, int y, int z, byte pitch, byte yaw, int dataSize, short dataOne, short dataTwo, short dataThree) {
		this.entityId = entityId;
		this.entityType = entityType;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.dataSize = dataSize;
		this.dataOne = dataOne;
		this.dataTwo = dataTwo;
		this.dataThree = dataThree;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		wrapper.writeVarInt(entityId);
		wrapper.writeByte(entityType);
		wrapper.writeInt(x);
		wrapper.writeInt(y);
		wrapper.writeInt(z);
		wrapper.writeByte(pitch);
		wrapper.writeByte(yaw);

		wrapper.writeInt(dataSize);
		if (dataSize > 0) {
			wrapper.writeShort(dataOne);
			wrapper.writeShort(dataTwo);
			wrapper.writeShort(dataThree);
		}
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(entityId) + 3 + (getIntSize() * 4) + ((dataSize > 0) ? (getShortSize() * 3) : 0) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0E;
	}
}
