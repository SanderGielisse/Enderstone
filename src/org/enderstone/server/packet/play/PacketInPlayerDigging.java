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
import org.enderstone.server.Main;
import org.enderstone.server.entity.EntityItem;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.BlockId;

public class PacketInPlayerDigging extends Packet {

	private byte status;
	private Location loc;
	private byte face;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.status = buf.readByte();
		this.loc = readLocation(buf);
		this.face = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return 2 + getLocationSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x07;
	}

	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				int x = getLocation().getBlockX();
				int y = getLocation().getBlockY();
				int z = getLocation().getBlockZ();
				
				short blockId = Main.getInstance().mainWorld.getBlockIdAt(x, y, z).getId();
				if (getStatus() == 2) {
					if (networkManager.player.getLocation().isInRange(6, loc)) {
						Main.getInstance().mainWorld.setBlockAt(x, y, z, BlockId.AIR, (byte) 0);
					}
					Main.getInstance().mainWorld.broadcastSound("dig.grass", x, y, z, 1F, (byte) 63, loc, networkManager.player);
					Main.getInstance().mainWorld.addEntity(new EntityItem(loc, new ItemStack((short)2, (byte) 4, (short) 0)));
				}
			}
		});
	}

	public byte getStatus() {
		return status;
	}

	public Location getLocation(){
		return loc;
	}

	public byte getFace() {
		return face;
	}
}
