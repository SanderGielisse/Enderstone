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
import org.enderstone.server.Location;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

/**
 *
 * @author Fernando
 */
public class PacketInTabComplete extends Packet {

	private String halfCommand;
	private boolean hasPosition;
	private Location lookingAt;
	
	@Override
	public void read(ByteBuf buf) throws IOException {
		halfCommand = readString(buf);
		hasPosition = buf.readBoolean();
		if(hasPosition){
			lookingAt = readLocation(buf);
		}
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(halfCommand) + 1 + (hasPosition ? getLocationSize() : 0) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x14;
	}
	
	public String getHalfCommand() {
		return halfCommand;
	}
	
	public boolean hasPosition() {
		return hasPosition;
	}

	public Location getLookingAt() {
		return lookingAt;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		networkManager.player.onPlayerChatComplete(this);
	}
	
}
