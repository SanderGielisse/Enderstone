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
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.blocks.BlockDefinition;
import org.enderstone.server.blocks.BlockDefinitions;
import org.enderstone.server.entity.EntityItem;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderWorld;

public class PacketInPlayerDigging extends Packet {

	private byte status;
	private Location loc;
	private byte face;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.status = wrapper.readByte();
		this.loc = wrapper.readLocation();
		this.face = wrapper.readByte();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
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

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				int x = getLocation().getBlockX();
				int y = getLocation().getBlockY();
				int z = getLocation().getBlockZ();
				
				EnderWorld world = Main.getInstance().getWorld(networkManager.player);
				loc.setWorld(world);
				short blockId = world.getBlockIdAt(x, y, z).getId();

				BlockDefinition definition = BlockDefinitions.getBlock(world.getBlockIdAt(x, y, z));

				if (getStatus() == 0) {
					if (BlockId.byId(blockId).doesInstantBreak()) {
						status = (byte) 2;
						onRecieve(networkManager);
					}
				} else if (getStatus() == 2) {
					if (networkManager.player.getLocation().isInRange(6, loc, true)) {

						if (definition.canBreak(networkManager.player, world, x, y, z)) {

							world.setBlockAt(x, y, z, BlockId.AIR, (byte) 0);

							world.broadcastSound(definition.getBreakSound(), 1F, (byte) 63, loc, networkManager.player);
							Location loca = loc.clone();
							loca.setX(loca.getX() + 0.5);
							loca.setZ(loca.getZ() + 0.5);
							world.addEntity(new EntityItem(world,loca, definition.getDrop(networkManager.player, world, x, y, z), 1, new Vector(0, 0.1D, 0)));
						}
					}
				} else if (getStatus() == 3) {
					networkManager.player.getInventoryHandler().recievePacket(PacketInPlayerDigging.this);
				} else if (getStatus() == 4) {
					networkManager.player.getInventoryHandler().recievePacket(PacketInPlayerDigging.this);
				} else if (getStatus() == 5){
					networkManager.player.clientSettings.isEatingTicks = 0;
					networkManager.player.updateDataWatcher();
					networkManager.player.getWorld().broadcastPacket(new PacketOutEntityMetadata(networkManager.player.getEntityId(), networkManager.player.getDataWatcher()), networkManager.player.getLocation());
				}
			}
		});
	}

	public byte getStatus() {
		return status;
	}

	public Location getLocation() {
		return loc;
	}

	public byte getFace() {
		return face;
	}

	@Override
	public String toString() {
		return "PacketInPlayerDigging{" + "status=" + status + ", loc=" + loc + ", face=" + face + '}';
	}
}
