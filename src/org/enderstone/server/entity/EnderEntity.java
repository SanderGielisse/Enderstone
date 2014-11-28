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

import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Entity;
import org.enderstone.server.api.event.entity.EntityDamageEvent;
import org.enderstone.server.api.event.entity.EntityDeathEvent;
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutAnimation;
import org.enderstone.server.packet.play.PacketOutEntityMetadata;
import org.enderstone.server.packet.play.PacketOutEntityStatus;
import org.enderstone.server.packet.play.PacketOutEntityVelocity;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderWorld;

public abstract class EnderEntity implements Entity {

	private static int entityCount = 0;

	private final int entityId;
	private final Location location;
	private float health = Float.NaN;
	private float maxHealth = Float.NaN;
	private DataWatcher dataWatcher = new DataWatcher();
	private long latestDamage = 0;
	private int fireTicks = 0;

	private boolean shouldBeRemoved = false;
	private boolean broadcastDespawn = true;

	public EnderEntity(Location location) {
		this.entityId = entityCount++;
		this.location = location.clone();
	}

	public int getEntityId() {
		return entityId;
	}

	public Location getLocation() {
		return location.clone();
	}

	public void setLocation(Location newLoc) {
		this.location.cloneFrom(newLoc);
	}

	public abstract Packet[] getSpawnPackets();

	@Override
	public abstract void teleport(Location loc);

	@Override
	public abstract void teleport(EnderEntity entity);

	public abstract void onRightClick(EnderPlayer attacker);

	public abstract void onLeftClick(EnderPlayer attacker);

	public boolean onCollision(EnderPlayer withPlayer) {
		return false;
	}

	public boolean damage(float damage) {
		EntityDamageEvent e = new EntityDamageEvent(this, damage);
		if (Main.getInstance().callEvent(e)) {
			return health != 0;
		}
		if (Float.isNaN(this.health))
			initHealth();
		if (health == 0)
			return false;
		for (EnderPlayer p : Main.getInstance().onlinePlayers) {
			if (p.getLocation().isInRange(25, getLocation(), true)) {
				p.getNetworkManager().sendPacket(new PacketOutEntityStatus(getEntityId(), PacketOutEntityStatus.Status.LIVING_ENTITY_HURT));
				p.getNetworkManager().sendPacket(new PacketOutSoundEffect(isDead() ? getDeadSound() : getDamageSound(), location));
				p.getNetworkManager().sendPacket(new PacketOutAnimation(getEntityId(), (byte) 1));
			}
		}
		boolean death = this.setHealth(Math.max(health - e.getDamage(), 0));
		if (death) {
			Main.getInstance().callEvent(new EntityDeathEvent(this));
		}
		return death;
	}

	public boolean damage(float damage, Vector knockback) {
		// damage delay
		if ((Main.getInstance().getCurrentServerTick() - this.latestDamage) < 10) {
			return false;
		}
		this.latestDamage = Main.getInstance().getCurrentServerTick();

		boolean death = this.damage(damage);
		((EnderWorld) this.getWorld()).broadcastPacket(new PacketOutEntityVelocity(this.getEntityId(), knockback), this.getLocation());
		this.location.add(knockback.getX(), knockback.getY(), knockback.getZ());
		return death;
	}

	public final boolean isDead() {
		if (Float.isNaN(this.health))
			initHealth();
		return this.health <= 0;
	}

	public final void kill() {
		damage(1000);
	}

	public final void heal() {
		setHealth(this.getMaxHealth());
		setFireTicks(0);
	}

	protected void onHealthUpdate(float newHealth, float lastHealth) {

	}

	public float getHealth() {
		if (Float.isNaN(this.health))
			initHealth();
		return health;
	}

	public final float getMaxHealth() {
		if (Float.isNaN(this.health))
			initHealth();
		return maxHealth;
	}

	public final void setMaxHealth(float health) {
		if (Float.isNaN(this.health))
			initHealth();
		setHealth(Math.min(health, this.health));
		this.maxHealth = health;
	}

	public final boolean setHealth(float health) {
		if (Float.isNaN(this.health))
			initHealth();
		if (health == this.health)
			return false;
		if (health > this.maxHealth)
			throw new IllegalArgumentException("Health value too large");
		float lastHealth = this.health;
		this.health = health;
		onHealthUpdate(this.health, lastHealth);
		return this.health <= 0;
	}

	@Override
	public void remove() {
		this.shouldBeRemoved = true;
		this.broadcastDespawn = true;
	}

	public void removeInternally(boolean broadcastDespawn) {
		this.shouldBeRemoved = true;
		this.broadcastDespawn = broadcastDespawn;
	}

	protected abstract String getDamageSound();

	protected abstract String getDeadSound();

	protected abstract String getRandomSound();

	protected abstract float getBaseHealth();

	protected abstract float getBaseMaxHealth();

	public abstract void updatePlayers(Set<EnderPlayer> onlinePlayers);

	public abstract void broadcastLocation(Location newLocation);

	public abstract void broadcastRotation(float pitch, float yaw);

	public abstract boolean isValid();

	public abstract void updateDataWatcher();

	public abstract void onSpawn();

	public abstract float getWidth();

	public abstract float getHeight();

	private void initHealth() {
		this.health = getBaseHealth();
		this.maxHealth = getBaseMaxHealth();
	}

	public DataWatcher getDataWatcher() {
		return this.dataWatcher;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entityId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnderEntity other = (EnderEntity) obj;
		if (entityId != other.entityId)
			return false;
		return true;
	}

	public void serverTick() {
		if (shouldBeRemoved || this.isDead() || Main.getInstance().doPhysics == false) {
			return;
		}
		// if (this instanceof EnderPlayer) {
		// EnderLogger.debug("FireTicks: " + fireTicks);
		// }
		World world = this.getWorld();
		double x = this.getLocation().getX();
		double y = this.getLocation().getY();
		double z = this.getLocation().getZ();
		BlockId id1 = world.getBlock(floor(x), floor(y), floor(z)).getBlock();
		BlockId id2 = world.getBlock(floor(x + (getWidth() / 2)), floor(y), floor(z)).getBlock();
		BlockId id3 = world.getBlock(floor(x - (getWidth() / 2)), floor(y), floor(z)).getBlock();
		BlockId id4 = world.getBlock(floor(x), floor(y), floor(z - (getWidth() / 2))).getBlock();
		BlockId id5 = world.getBlock(floor(x), floor(y), floor(z + (getWidth() / 2))).getBlock();
		BlockId[] array = new BlockId[] { id1, id2, id3, id4, id5 };

		boolean isInFire = compare(BlockId.FIRE, array) || compare(BlockId.LAVA, array) || compare(BlockId.LAVA_FLOWING, array);
		boolean isInWater = compare(BlockId.WATER, array) || compare(BlockId.WATER_FLOWING, array);
		if (isInWater) {
			if (this.getFireTicks() > 0) {
				this.setFireTicks(0);
			}
		} else if (isInFire) {
			if (getFireTicks() == 0) {
				setFireTicks(100); // 5 seconds burn after leaving lava/fire
			}
		}

		if (fireTicks == 0) {
			return;
		}
		this.fireTicks--;
		if (fireTicks % 10 == 0) {
			if (damage(1F)) {
				if ((this instanceof EnderPlayer)) {
					Main.getInstance().broadcastMessage(new AdvancedMessage(((EnderPlayer) this).getPlayerName() + " burned to death."));
				}
			}
		}
		if (this.fireTicks == 0) {
			this.setFireTicks(0);
		}
	}

	public static int floor(double num) {
		final int floor = (int) num;
		return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
	}

	private static boolean compare(BlockId fire, BlockId[] ids) {
		for (BlockId id : ids) {
			if (id == fire) {
				return true;
			}
		}
		return false;
	}

	public int getFireTicks() {
		return fireTicks;
	}

	public void setFireTicks(int fireTicks) {
		if (this.fireTicks > 0 && fireTicks > 0) {
			this.fireTicks = fireTicks;
			return;
		}
		if (this.fireTicks == 0 && fireTicks == 0) {
			return;
		}
		this.fireTicks = fireTicks;
		this.updateDataWatcher();
		this.getLocation().getWorld().broadcastPacket(new PacketOutEntityMetadata(getEntityId(), dataWatcher), this.getLocation());
	}

	protected static byte calcYaw(float f) {
		int i = (int) f;
		return (byte) (f < i ? i - 1 : i);
	}

	public boolean shouldBeRemoved() {
		return this.shouldBeRemoved;
	}

	public boolean shouldBroadcastDespawn() {
		return this.broadcastDespawn;
	}

	@Override
	public Location getHeadLocation() {
		return this.getLocation().add(0, this.getHeight(), 0);
	}
}
