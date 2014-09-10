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
package org.enderstone.server.entity;

import java.util.Set;
import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.entity.Item;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutCollectItem;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityMetadata;
import org.enderstone.server.packet.play.PacketOutSpawnObject;
import org.enderstone.server.regions.EnderWorld;

public class EntityItem extends EnderEntity implements Item {

	private final EnderWorld world;
	private final ItemStack itemstack;
	private int pickupDelay;
	
	public EntityItem(EnderWorld world, Location location, ItemStack stack, int pickupDelay) {
		super(location);
		this.itemstack = stack;
		this.pickupDelay = pickupDelay;
		this.world = world;
		onSpawn(); // must be called from main thread
	}

	@Override
	public void onSpawn() {
		this.updateDataWatcher();
		this.updatePlayers(Main.getInstance().onlinePlayers);
	}

	@Override
	public Packet getSpawnPacket() {
		Location loc = this.getLocation();
		return new PacketOutSpawnObject(getEntityId(), (byte) 2, (int) ((loc.getX()) * 32.0D), (int) ((loc.getY() + 0.25) * 32.0D), (int) ((loc.getZ()) * 32.0D), (byte) 0, (byte) 0, 1, (short) 0, (short) 0, (short) 0);
	}

	@Override
	public void teleport(Location loc) {
	}

	@Override
	public void teleport(EnderEntity entity) {
	}

	@Override
	public void onRightClick(EnderPlayer attacker) {
	}

	@Override
	public void onLeftClick(EnderPlayer attacker) {
	}

	@Override
	protected String getDamageSound() {
		return "";
	}

	@Override
	protected String getDeadSound() {
		return "";
	}

	@Override
	protected float getBaseHealth() {
		return 1;
	}

	@Override
	protected float getBaseMaxHealth() {
		return 1;
	}

	@Override
	public void updatePlayers(Set<EnderPlayer> onlinePlayers) {
		Packet spawnPacket = this.getSpawnPacket();
		Packet packet = new PacketOutEntityMetadata(this.getEntityId(), this.getDataWatcher());
		for (EnderPlayer pl : onlinePlayers) {
			if (pl.getLocation().isInRange(40, getLocation(), true) && !pl.canSeeEntity.contains(this)) {
				pl.canSeeEntity.add(this);
				pl.getNetworkManager().sendPacket(spawnPacket);
				pl.getNetworkManager().sendPacket(packet);
			} else if (!pl.getLocation().isInRange(40, this.getLocation(), true) && pl.canSeeEntity.contains(this)) {
				pl.canSeeEntity.remove(this);
				pl.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[] { this.getEntityId() }));
			} else if (pl.canSeeEntity.contains(this) && !Main.getInstance().getWorld(pl).entities.contains(this)) {
				pl.canSeeEntity.remove(this);
				pl.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[] { this.getEntityId() }));
			}
		}
	}

	@Override
	public void broadcastLocation(Location newLocation) {
	}

	@Override
	public void broadcastRotation(float pitch, float yaw) {
	}

	@Override
	public boolean isValid() {
		return !isDead();
	}

	@Override
	public void updateDataWatcher() {
		this.getDataWatcher().watch(0, (byte) 0);
		this.getDataWatcher().watch(1, (short) 300);
		this.getDataWatcher().watch(10, this.itemstack);
	}

	@Override
	public boolean onCollision(EnderPlayer withPlayer) {
		if (pickupDelay <= 0) {
			if (withPlayer.canSeeEntity.contains(this)) {
				ItemStack stack = withPlayer.getInventoryHandler().tryPickup(this.itemstack);
				if (stack == null) {
					withPlayer.canSeeEntity.remove(this);
					Main.getInstance().getWorld(withPlayer).broadcastPacket(new PacketOutCollectItem(this.getEntityId(), withPlayer.getEntityId()), withPlayer.getLocation());
					return true;
				}
			}
		}
		return false;
	}
	
	private int gravityCheck;
	
	@Override
	public void serverTick() {
		if (pickupDelay > 0) {
			pickupDelay--;
		}

		//TODO rewrite this, not even close to working properly :p
		if (gravityCheck++ % 20 == 0) {
			Location loc = getLocation().clone();
			loc.add(0, -1, 0);

			if (world.getBlockIdAt(loc).getId() == 0) {
				while (world.getBlockIdAt(loc.add(0, -1, 0)).getId() == 0) { //TODO make it able to fall through blocks such as torches etc.
					if (loc.getBlockY() < 0) {
						break;
					}
				}
				this.getLocation().setX(loc.getX());
				this.getLocation().setY(loc.getY());
				this.getLocation().setZ(loc.getZ());
				this.getLocation().setYaw(loc.getYaw());
				this.getLocation().setPitch(loc.getPitch());
			}
		}
	}

	@Override
	public EnderWorld getWorld() {
		return this.world;
	}

	@Override
	public ItemStack getItemStack() {
		return this.itemstack;
	}
}
