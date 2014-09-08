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
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.Utill;
import org.enderstone.server.entity.drops.EntityDrop;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityHeadLook;
import org.enderstone.server.packet.play.PacketOutEntityLook;
import org.enderstone.server.packet.play.PacketOutEntityRelativeMove;
import org.enderstone.server.packet.play.PacketOutEntityTeleport;
import org.enderstone.server.packet.play.PacketOutSpawnMob;
import org.enderstone.server.regions.EnderWorld;

public class EntityMob extends Entity {

	private byte appearanceId;
	private EnderWorld world;

	public EntityMob(byte appearanceId, EnderWorld world, Location location) {
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
	public Packet getSpawnPacket() {
		return new PacketOutSpawnMob(this.getEntityId(), this.appearanceId, (int) (getLocation().getX() * 32.0D), (int) (getLocation().getY() * 32.0D), (int) (getLocation().getZ() * 32.0D), (byte) 0, (byte) 0, (byte) 0, (short) 0, (short) 0, (short) 0, this.getDataWatcher());
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
	public void teleport(Entity entity) {
		teleport(entity.getLocation());
	}

	@Override
	public void onRightClick(EnderPlayer attacker) {
	}

	@Override
	public void onLeftClick(EnderPlayer attacker) {
		damage(1F);
	}

	@Override
	protected String getDamageSound() {
		return "game.hostile.hurt";
	}

	@Override
	protected String getDeadSound() {
		return "game.hostile.die";
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
				ep.getNetworkManager().sendPacket(this.getSpawnPacket());
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
		Packet pack1 = new PacketOutEntityLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F), (byte) Utill.calcYaw(pitch * 256.0F / 360.0F), false);
		Packet pack2 = new PacketOutEntityHeadLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F));

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
		drops.add(new EntityDrop(new ItemStack((short) 2, (byte) 1, (byte) 0), 100));
		return drops;
	}

	@Override
	protected void onHealthUpdate(float newHealth, float lastHealth) {
		if (newHealth <= 0) {
			// entity died, remove it
			world.removeEntity(this);

			// do entity drops
			List<EntityDrop> drops = this.getDrops();
			for (EntityDrop drop : drops) {
				if (Main.random.nextInt(100) <= drop.getDropChance()) {
					world.addEntity(new EntityItem(world, getLocation(), drop.getStack().clone(), 5));
				}
			}
		}
	}
}
