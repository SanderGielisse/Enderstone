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

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutChunkData extends Packet {

	private int x;
	private int z;
	private boolean groundUpContinuous;
	private short primaryBitMap;
	private int size;
	private byte[] data;

	public PacketOutChunkData(int x, int z, boolean groundUpContinuous, short primaryBitMap, int size, byte[] data) {
		this.x = x;
		this.z = z;
		this.groundUpContinuous = groundUpContinuous;
		this.primaryBitMap = primaryBitMap;
		this.size = size;
		this.data = data;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(x);
		buf.writeInt(z);
		buf.writeBoolean(groundUpContinuous);
		buf.writeShort(primaryBitMap);
		writeVarInt(size, buf);
		buf.writeBytes(data, 0, size);
	}

	@Override
	public int getSize() throws IOException {
		return (2 * getIntSize()) + 1 + getShortSize() + getVarIntSize(size) + data.length + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x21;
	}

	private final static byte[] emptyChunk;

	static {
		emptyChunk = new byte[256];
	}

	public static PacketOutChunkData clearChunk(int x, int z) {
		return new PacketOutChunkData(x, z, true, (short) 0, emptyChunk.length, emptyChunk);
	}

	@Override
	public String toString() {
		return "PacketOutChunkData{" + "x=" + x + ", z=" + z + ", size=" + size + '}';
	}

}
