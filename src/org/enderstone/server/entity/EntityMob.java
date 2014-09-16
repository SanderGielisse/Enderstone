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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.entity.drops.EntityDrop;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityHeadLook;
import org.enderstone.server.packet.play.PacketOutEntityLook;
import org.enderstone.server.packet.play.PacketOutEntityRelativeMove;
import org.enderstone.server.packet.play.PacketOutEntityStatus;
import org.enderstone.server.packet.play.PacketOutEntityTeleport;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.packet.play.PacketOutSpawnMob;
import org.enderstone.server.packet.play.PacketOutEntityStatus.Status;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderWorld;

public abstract class EntityMob extends EnderEntity {

	private byte appearanceId;
	private EnderWorld world;

	EntityMob(byte appearanceId, EnderWorld world, Location location) {
		super(location);
		this.world = world;
		this.appearanceId = appearanceId;
		this.onSpawn();
	}

	@Override
	public void onSpawn() {
		this.updateDataWatcher();
		this.updatePlayers(Main.getInstance().onlinePlayers);
	}

	@Override
	public Packet[] getSpawnPackets() {
		return new Packet[] {
				new PacketOutSpawnMob(this.getEntityId(), this.appearanceId, (int) (getLocation().getX() * 32.0D), (int) (getLocation().getY() * 32.0D), (int) (getLocation().getZ() * 32.0D), (byte) 0, (byte) 0, (byte) 0, (short) 0, (short) 0, (short) 0, this.getDataWatcher())
		};
	}

	@Override
	public void teleport(Location loc) {
		world.broadcastPacket(new PacketOutEntityTeleport(getEntityId(), (int) (loc.getX() * 32.0D), (int) (loc.getY() * 32.0D), (int) (loc.getZ() * 32.0D), (byte) 0, (byte) 0, false), this.getLocation());
		Location oldLoc = this.getLocation();
		oldLoc.setX(loc.getX());
		oldLoc.setY(loc.getY());
		oldLoc.setZ(loc.getZ());
		oldLoc.setPitch(loc.getPitch());
		oldLoc.setYaw(loc.getYaw());
	}

	@Override
	public void teleport(EnderEntity entity) {
		teleport(entity.getLocation());
	}

	@Override
	public void onRightClick(EnderPlayer attacker) {
	}

	@Override
	public void onLeftClick(EnderPlayer attacker) {
		this.damage(5F, Vector.substract(attacker.getLocation(), this.getLocation()).normalize(this.getLocation().distance(attacker.getLocation()) * 2));
	}

	@Override
	protected float getBaseHealth() {
		return 20;
	}

	@Override
	protected float getBaseMaxHealth() {
		return 20;
	}

	@Override
	public void updatePlayers(Set<EnderPlayer> onlinePlayers) {
		for (EnderPlayer ep : onlinePlayers) {
			if (ep.getLocation().isInRange(40, this.getLocation(), false) && !ep.canSeeEntity.contains(this)) {
				ep.canSeeEntity.add(this);
				ep.getNetworkManager().sendPacket(this.getSpawnPackets());
			} else if (!ep.getLocation().isInRange(40, this.getLocation(), false) && ep.canSeeEntity.contains(this)) {
				ep.canSeeEntity.remove(this);
				ep.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[] { this.getEntityId() }));
			} else if (ep.canSeeEntity.contains(this) && !Main.getInstance().getWorld(ep).entities.contains(this)) {
				ep.canSeeEntity.remove(this);
				ep.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[] { this.getEntityId() }));
			}
		}
	}

	private int moveUpdates = 0;

	@Override
	public void broadcastLocation(Location newLocation) {
		double dx = (newLocation.getX() - this.getLocation().getX()) * 32;
		double dy = (newLocation.getY() - this.getLocation().getY()) * 32;
		double dz = (newLocation.getZ() - this.getLocation().getZ()) * 32;

		Packet packet;

		if (moveUpdates++ % 40 == 0 || dx > 127 || dx < -127 || dy > 127 || dy < -127 || dz > 127 || dz < -127) {
			// teleport
			packet = new PacketOutEntityTeleport(this.getEntityId(), (int) (this.getLocation().getX() * 32.0D), (int) (this.getLocation().getY() * 32.0D), (int) (this.getLocation().getZ() * 32.0D), (byte) this.getLocation().getYaw(), (byte) this.getLocation().getPitch(), false);
		} else {
			// movement
			packet = new PacketOutEntityRelativeMove(this.getEntityId(), (byte) dx, (byte) dy, (byte) dz, false);
		}

		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if (ep.canSeeEntity.contains(this)) {
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}

	@Override
	public void broadcastRotation(float pitch, float yaw) {
		Packet pack1 = new PacketOutEntityLook(this.getEntityId(), (byte) calcYaw(yaw * 256.0F / 360.0F), (byte) calcYaw(pitch * 256.0F / 360.0F), false);
		Packet pack2 = new PacketOutEntityHeadLook(this.getEntityId(), (byte) calcYaw(yaw * 256.0F / 360.0F));

		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if (ep.canSeeEntity.contains(this)) {
				ep.getNetworkManager().sendPacket(pack1);
				ep.getNetworkManager().sendPacket(pack2);
			}
		}
	}

	@Override
	public boolean isValid() {
		return !isDead();
	}

	@Override
	public void updateDataWatcher() {
		this.getDataWatcher().watch(0, (byte) 0);
		this.getDataWatcher().watch(1, (short) 1);
		this.getDataWatcher().watch(6, getHealth());
	}

	public List<EntityDrop> getDrops() {
		List<EntityDrop> drops = new ArrayList<EntityDrop>();
		drops.add(new EntityDrop(new ItemStack(BlockId.DIAMOND_BLOCK), 1));
		drops.add(new EntityDrop(new ItemStack(BlockId.COOKED_BEEF), 20));
		drops.add(new EntityDrop(new ItemStack(BlockId.COOKED_CHICKEN), 20));
		drops.add(new EntityDrop(new ItemStack(BlockId.COOKED_FISH), 20));
		drops.add(new EntityDrop(new ItemStack(BlockId.COOKED_MUTTON), 20));
		drops.add(new EntityDrop(new ItemStack(BlockId.COOKED_RABBIT), 20));
		return drops;
	}

	@Override
	protected void onHealthUpdate(float newHealth, float lastHealth) {
		if (newHealth <= 0) {
			this.getWorld().broadcastPacket(new PacketOutEntityStatus(this.getEntityId(), Status.LIVING_ENTITY_DEAD), this.getLocation());
			// entity died, remove it
			world.removeEntity(this, false);

			// do entity drops
			List<EntityDrop> drops = this.getDrops();
			for (EntityDrop drop : drops) {
				if (Main.random.nextInt(100) <= drop.getDropChance()) {
					world.addEntity(new EntityItem(world, getLocation(), drop.getStack().clone(), 5, new Vector(0, 0.1D, 0)));
				}
			}
		}
	}

	@Override
	public EnderWorld getWorld() {
		return this.world;
	}
	
	private long latestSound = 0;
	
	@Override
	public void serverTick() {
		//added some temporarily mob sounds, just for fun :D
		if (latestSound++ % (20 * 10) == 0) {
			if (Main.random.nextBoolean()) {
				world.broadcastPacket(new PacketOutSoundEffect(this.getRandomSound(), this.getLocation()), this.getLocation());
			}
		}
		super.serverTick();
	}
}
