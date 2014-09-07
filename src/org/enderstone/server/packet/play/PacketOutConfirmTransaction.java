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

/**
 *
 * @author Fernando
 */
public class PacketOutConfirmTransaction extends Packet{

	public PacketOutConfirmTransaction(byte windowId, short actionNumber, boolean accepted) {
		this.windowId = windowId;
		this.actionNumber = actionNumber;
		this.accepted = accepted;
	}

	private byte windowId;
	private short actionNumber;
	private boolean accepted;
	
	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		wrapper.writeByte(windowId);
		wrapper.writeShort(actionNumber);
		wrapper.writeBoolean(accepted);
	}

	@Override
	public int getSize() throws IOException {
		return 1 + 2 + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x32;
	}
	
}
