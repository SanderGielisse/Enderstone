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

import org.enderstone.server.Main;
import org.enderstone.server.api.event.player.PlayerHeldItemChangeEvent;
import org.enderstone.server.entity.player.EquipmentUpdateType;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketInHeldItemChange extends Packet {

	private short slot;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.slot = wrapper.readShort();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getShortSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x09;
	}

	public short getSlot() {
		return slot;
	}
	
	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				if(Main.getInstance().callEvent(new PlayerHeldItemChangeEvent(networkManager.player, (short) networkManager.player.getInventory().getHeldItemSlot(), getSlot()))){
					return;
				}
				networkManager.player.getInventory().recievePacket(PacketInHeldItemChange.this);
				networkManager.player.broadcastEquipment(EquipmentUpdateType.ITEM_IN_HAND_CHANGE);
			}
		});
	}

	@Override
	public String toString() {
		return "PacketInHeldItemChange{" + "slot=" + slot + '}';
	}
}
