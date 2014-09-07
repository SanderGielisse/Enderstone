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
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketInClickWindow extends Packet {

	private byte windowId;
	private short slot;
	private byte button;
	private short actionNumber;
	private byte mode;
	private ItemStack itemStack;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.windowId = wrapper.readByte();
		this.slot = wrapper.readShort();
		this.button = wrapper.readByte();
		this.actionNumber = wrapper.readShort();
		this.mode = wrapper.readByte();
		this.itemStack = wrapper.readItemStack();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return 3 + (getShortSize() * 2) + getItemStackSize(itemStack) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0E;
	}
	
	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				networkManager.player.getInventoryHandler().recievePacket(PacketInClickWindow.this);
			}
		});
	}

	public byte getWindowId() {
		return windowId;
	}

	public short getSlot() {
		return slot;
	}

	public byte getButton() {
		return button;
	}

	public short getActionNumber() {
		return actionNumber;
	}

	public byte getMode() {
		return mode;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public String toString() {
		return "PacketInClickWindow{" + "windowId=" + windowId + ", slot=" + slot + ", button=" + button + ", actionNumber=" + actionNumber + ", mode=" + mode + ", itemStack=" + itemStack + '}';
	}
}
