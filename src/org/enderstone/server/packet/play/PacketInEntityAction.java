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
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketInEntityAction extends Packet {

	private int entityId;
	private byte actionId;
	private int jumpBoost;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.entityId = wrapper.readVarInt();
		this.actionId = wrapper.readByte();
		this.jumpBoost = wrapper.readVarInt();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(entityId) + 1 + getVarIntSize(jumpBoost) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0B;
	}

	public int getEntityId() {
		return entityId;
	}

	public byte getActionId() {
		return actionId;
	}

	public int getJumpBoost() {
		return jumpBoost;
	}
	
	@Override
	public void onRecieve(final NetworkManager networkManager) {
		// actionId list:
		// 0: Crouch
		// 1: Uncrouch
		// 2: Leave Bed
		// 3: Start Sprint
		// 4: End Sprint
		// 5: Horse Jump
		// 6: Open Inventory
		
		Main.getInstance().sendToMainThread(new Runnable() {
			
			@Override
			public void run() {
				if(getActionId() == 0){
					networkManager.player.setSneaking(true);
				}else if(getActionId() == 1){
					networkManager.player.setSneaking(false);
				}else if(getActionId() == 3){
					networkManager.player.clientSettings.isSprinting = true;
				}else if(getActionId() == 4){
					networkManager.player.clientSettings.isSprinting = false;
				}
			}
		});
	}
}
