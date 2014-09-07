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

public class PacketInAbilities extends Packet {

	private byte flags;
	private float flySpeed;
	private float walkingSpeed;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.flags = wrapper.readByte();
		this.flySpeed = wrapper.readFloat();
		this.walkingSpeed = wrapper.readFloat();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return 1 + (getFloatSize() * 2) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x13;
	}

	public byte getFlags() {
		return flags;
	}

	public float getFlySpeed() {
		return flySpeed;
	}

	public float getWalkingSpeed() {
		return walkingSpeed;
	}
}
