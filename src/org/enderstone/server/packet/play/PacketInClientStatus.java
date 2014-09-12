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
import org.enderstone.server.api.GameMode;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketInClientStatus extends Packet {

	private int actionId;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.actionId = wrapper.readUnsignedByte();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x16;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		if (getActionId() == 0) {
			//respawn
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					if (networkManager.player.isDead()) {
						networkManager.sendPacket(new PacketOutRespawn(0, (byte) 0, (byte) networkManager.player.clientSettings.gameMode.getId(), "default"));
						networkManager.player.teleport(networkManager.player.getWorld().getSpawn().clone());
						networkManager.player.getInventoryHandler().updateInventory();
						networkManager.player.heal();
						networkManager.player.onRespawn();
					}
				}
			});
		}
	}

	public int getActionId() {
		return actionId;
	}
}
