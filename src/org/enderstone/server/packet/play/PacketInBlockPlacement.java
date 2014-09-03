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
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.BlockId;

public class PacketInBlockPlacement extends Packet {

	private Location loc;
	private byte direction;
	private ItemStack heldItem;
	private byte cursorX;
	private byte cursorY;
	private byte cursorZ;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.loc = readLocation(buf);
		this.direction = buf.readByte();
		this.heldItem = readItemStack(buf);
		this.cursorX = buf.readByte(); // The position of the crosshair on the block
		this.cursorY = buf.readByte();
		this.cursorZ = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getLocationSize() + 4 + getItemStackSize(heldItem) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x08;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		if (getHeldItem() == null || getHeldItem().getBlockId() == -1) {
			return;
		}
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				int x = getLocation().getBlockX();
				int y = getLocation().getBlockY();
				int z = getLocation().getBlockZ();

				byte direct = getDirection();
				if (direct == 0) {
					y--;
				} else if (direct == 1) {
					y++;
				} else if (direct == 2) {
					z--;
				} else if (direct == 3) {
					z++;
				} else if (direct == 4) {
					x--;
				} else if (direct == 5) {
					x++;
				}
				if (networkManager.player.getLocation().isInRange(6, loc)) {
					ItemStack stack = getHeldItem();
					if (stack.getBlockId() == BlockId.LAVA_BUCKET.getId()) {
						stack.setBlockId(BlockId.LAVA.getId());
					} else if (stack.getBlockId() == BlockId.WATER_BUCKET.getId()) {
						stack.setBlockId(BlockId.WATER.getId());
					} else if (stack.getBlockId() == BlockId.FLINT_AND_STEEL.getId()) {
						stack.setBlockId(BlockId.FIRE.getId());
					}
					Main.getInstance().mainWorld.setBlockAt(x, y, z, BlockId.byId(getHeldItem().getBlockId()), (byte) getHeldItem().getDamage());
				}
				Main.getInstance().mainWorld.broadcastSound("dig.grass", x, y, z, 1F, (byte) 63, loc, null);
			}
		});
	}

	public Location getLocation(){
		return loc;
	}

	public byte getDirection() {
		return direction;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}

	public byte getCursorX() {
		return cursorX;
	}

	public byte getCursorY() {
		return cursorY;
	}

	public byte getCursorZ() {
		return cursorZ;
	}
}
