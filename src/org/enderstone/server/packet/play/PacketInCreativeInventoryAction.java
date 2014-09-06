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
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInCreativeInventoryAction extends Packet {

	private short slot;
	private ItemStack clicked;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.slot = buf.readShort();
		this.clicked = readItemStack(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getShortSize() + getItemStackSize(clicked) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x10;
	}

	public short getSlot() {
		return slot;
	}

	public ItemStack getClicked() {
		return clicked;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				networkManager.player.getInventoryHandler().recievePacket(PacketInCreativeInventoryAction.this);
			}
		});
	}

	@Override
	public String toString() {
		return "PacketInCreativeInventoryAction{" + "slot=" + slot + ", clicked=" + clicked + '}';
	}
}
