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
import org.enderstone.server.api.Location;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketOutPlayParticle extends Packet {

	private int particleId;
	private boolean longDistance;
	private float x;
	private float y;
	private float z;
	private float xOffset;
	private float yOffset;
	private float zOffset;
	private float particleData;
	private int amount;
	private int[] data;

	public PacketOutPlayParticle(int particleId, boolean longDistance, float x, float y, float z, float xOffset, float yOffset, float zOffset, float particleData, int amount, int[] data) {
		this.particleId = particleId;
		this.longDistance = longDistance;
		this.x = x;
		this.y = y;
		this.z = z;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.particleData = particleData;
		this.amount = amount;
		this.data = data;
	}

	public PacketOutPlayParticle(int particleId, Location location, float xOffset, float yOffset, float zOffset, float data, int amount) {
		this.particleId = particleId;
		this.longDistance = false;
		this.x = (float) location.getX();
		this.y = (float) location.getY();
		this.z = (float) location.getZ();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.particleData = data;
		this.amount = amount;
		this.data = new int[0];
	}

	@Override
	public void read(PacketDataWrapper buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper buf) throws IOException {
		buf.writeInt(particleId);
		buf.writeBoolean(longDistance);
		buf.writeFloat(x);
		buf.writeFloat(y);
		buf.writeFloat(z);
		buf.writeFloat(xOffset);
		buf.writeFloat(yOffset);
		buf.writeFloat(zOffset);
		buf.writeFloat(particleData);
		buf.writeInt(amount);
		for (int i : data) {
			buf.writeVarInt(i);
		}
	}

	@Override
	public int getSize() throws IOException {
		int size = 0;
		for (int i : data) {
			size += getVarIntSize(i);
		}
		return getIntSize() + 1 + (getFloatSize() * 7) + getIntSize() + size + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x2A;
	}
}
