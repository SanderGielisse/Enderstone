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
import org.enderstone.server.Main;
import org.enderstone.server.inventory.Inventory.InventoryType;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.BlockId;

public class PacketOutRespawn extends Packet {

	private int dimension;
	private byte difficulty;
	private byte gamemode;
	private String levelType;

	public PacketOutRespawn(int dimension, byte difficulty, byte gamemode, String levelType) {
		this.dimension = dimension;
		this.difficulty = difficulty;
		this.gamemode = gamemode;
		this.levelType = levelType;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(dimension);
		buf.writeByte(difficulty);
		buf.writeByte(gamemode);
		writeString(levelType, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 2 + getStringSize(levelType) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x07;
	}
	
	@Override
	public void onSend(final NetworkManager networkManager) {
		if(!Thread.currentThread().equals(Main.getInstance().mainThread)){
			Main.getInstance().sendToMainThread(new Runnable() {
				
				@Override
				public void run() {
					//refillInventory(networkManager);
				}
			});
			return;
		}
		//refillInventory(networkManager);
	}
	
	public void refillInventory(NetworkManager networkManager){
		networkManager.player.inventory.setItem(InventoryType.HOTBAR, 5, new ItemStack(BlockId.DIRT.getId(), (byte) 101, (short) 1));
	}
}
