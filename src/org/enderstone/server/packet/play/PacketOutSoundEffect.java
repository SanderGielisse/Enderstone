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
import org.enderstone.server.Location;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketOutSoundEffect extends Packet {

	private String soundName;
	private int x;
	private int y;
	private int z;
	private float volume;
	private byte pitch;

	public PacketOutSoundEffect(String soundName, Location loc)
	{
		this(soundName,loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),1f,(byte)63);
	}
	
	public PacketOutSoundEffect(String soundName, int x, int y, int z, float volume, byte pitch) {
		this.soundName = soundName;
		this.x = x * 8;
		this.y = y * 8;
		this.z = z * 8;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		wrapper.writeString(soundName);
		wrapper.writeInt(x);
		wrapper.writeInt(y);
		wrapper.writeInt(z);
		wrapper.writeFloat(volume);
		wrapper.writeByte(pitch);	
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(soundName) + (getIntSize() * 3) + getFloatSize() + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x29;
	}
}
