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
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.inventory.InventoryType;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

/**
 *
 * @author Fernando
 */
public class PacketOutOpenWindow extends Packet {
	private byte windowId;
	private InventoryType inventoryType;
	private Message windowMessage;
	private transient String windowMessageString;
	private byte maxSlots;
	private int entityId;

	public PacketOutOpenWindow(byte windowId, InventoryType inventoryType, Message windowMessage, byte maxSlots, int entityId) {
		this.windowId = windowId;
		this.inventoryType = inventoryType;
		this.windowMessage = windowMessage;
		this.maxSlots = maxSlots;
		this.entityId = entityId;
	}
	
	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		if(windowMessageString == null) windowMessageString = windowMessage.toMessageJson();
		wrapper.writeByte(windowId);
		wrapper.writeString(inventoryType.getInventoryType());
		wrapper.writeString(windowMessageString);
		wrapper.writeByte(maxSlots);
		if(inventoryType == InventoryType.ENTITY_HORSE) wrapper.writeVarInt(entityId);
	}

	@Override
	public int getSize() throws IOException {
		if(windowMessageString == null) windowMessageString = windowMessage.toMessageJson();
		return 1 + getStringSize(inventoryType.getInventoryType()) + getStringSize(windowMessageString) + 1 + (inventoryType == InventoryType.ENTITY_HORSE ? getIntSize() : 0) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x2D;
	}
	
}
