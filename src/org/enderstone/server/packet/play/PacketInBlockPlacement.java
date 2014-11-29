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
import org.enderstone.server.api.Block;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.event.player.PlayerEatEvent;
import org.enderstone.server.blocks.BlockDefinition;
import org.enderstone.server.blocks.BlockDefinitions;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.entity.player.FoodType;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.items.ItemDefinition;
import org.enderstone.server.items.ItemDefinitions;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;
import org.enderstone.server.regions.BlockId;

public class PacketInBlockPlacement extends Packet {

	private Location loc;
	private byte direction;
	private ItemStack heldItem;
	private byte cursorX;
	private byte cursorY;
	private byte cursorZ;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.loc = wrapper.readLocation();
		this.direction = wrapper.readByte();
		this.heldItem = wrapper.readItemStack();
		this.cursorX = wrapper.readByte(); // The position of the crosshair on the block
		this.cursorY = wrapper.readByte();
		this.cursorZ = wrapper.readByte();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
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
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				if (getLocation().getY() != -1) {
					Block block = networkManager.player.getWorld().getBlock(getLocation());
					BlockDefinition def = BlockDefinitions.getBlock(block.getBlock());
					boolean stop = def.onRightClick(networkManager.player, block);
					if (stop) {
						return;
					}
				}

				if (getHeldItem() == null || getHeldItem().getBlockId() == -1) {
					return;
				}

				int x = getLocation().getBlockX();
				int y = getLocation().getBlockY();
				int z = getLocation().getBlockZ();
				byte direct = getDirection();

				// called when started eating, pulling bow etc.
				if (x == -1 && z == -1 && direct == -1) {
					if (FoodType.fromBlockId(getHeldItem().getBlockId()) != null) {
						if (Main.getInstance().callEvent(new PlayerEatEvent(networkManager.player, getHeldItem()))) {
							return;
						}
						networkManager.player.clientSettings.isEatingTicks = 1;
						networkManager.player.updateDataWatcher();
						networkManager.player.getWorld().broadcastPacket(new PacketOutEntityMetadata(networkManager.player.getEntityId(), networkManager.player.getDataWatcher()), networkManager.player.getLocation());
					}
					return;
				}

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
				loc.setWorld(networkManager.player.getWorld());
				if (networkManager.player.getLocation().isInRange(6, loc, true)) {
					EnderPlayer pl = networkManager.player;
					if (getHeldItem() == null || pl.getInventory().getItemInHand() == null) {
						if (Main.getInstance().getWorld(pl).getBlockIdAt(x, y, z).getId() == 0) {
							pl.sendBlockUpdate(new Location(pl.getWorld(), x, y, z, (byte) 0, (byte) 0), (short) 0, (byte) 0); // tell client it failed and set the block back to air
						}
						return;
					}
					if (pl.getInventory().getItemInHand().getBlockId() != getHeldItem().getBlockId() && pl.getInventory().getItemInHand().getAmount() != getHeldItem().getAmount()) {
						if (Main.getInstance().getWorld(pl).getBlockIdAt(x, y, z).getId() == 0) {
							pl.sendBlockUpdate(new Location(pl.getWorld(), x, y, z, (byte) 0, (byte) 0), (short) 0, (byte) 0); // tell client it failed and set the block back to air
						}
						return;
					}
					if (BlockId.byId(getHeldItem().getBlockId()).isValidBlock()) {
						pl.getWorld().setBlockAt(x, y, z, BlockId.byId(getHeldItem().getBlockId()), (byte) getHeldItem().getDamage());
						BlockDefinition definition = BlockDefinitions.getBlock(networkManager.player.getWorld().getBlockIdAt(x, y, z));
						pl.getInventory().decreaseItemInHand(1);
						pl.getWorld().broadcastSound(definition.getPlaceSound(), 1F, (byte) 63, loc, null);
						return;
					} else {
						ItemDefinition definition = ItemDefinitions.getItem(getHeldItem().getId());
						definition.onRightClick(pl, pl.getWorld().getBlock(x, y, z));
						return;
					}
				}
			}
		});
	}

	public Location getLocation() {
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
