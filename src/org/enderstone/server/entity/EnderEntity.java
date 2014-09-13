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
import org.enderstone.server.api.Vector;
import org.enderstone.server.api.entity.Entity;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutAnimation;
import org.enderstone.server.packet.play.PacketOutEntityStatus;
import org.enderstone.server.packet.play.PacketOutEntityVelocity;
import org.enderstone.server.packet.play.PacketOutSoundEffect;

public abstract class EnderEntity implements Entity {

	private static int entityCount = 0;

	private final int entityId;
	private final Location location;
	private float health = Float.NaN;
	private float maxHealth = Float.NaN;
	private DataWatcher dataWatcher = new DataWatcher();
	private long latestDamage = 0;

	public EnderEntity(Location location) {
		this.entityId = entityCount++;
		this.location = location;
	}

	public int getEntityId() {
		return entityId;
	}

	public Location getLocation() {
		return location;
	}

	public abstract Packet getSpawnPacket();

	public abstract void teleport(Location loc);

	public abstract void teleport(EnderEntity entity);

	public abstract void onRightClick(EnderPlayer attacker);

	public abstract void onLeftClick(EnderPlayer attacker);

	public boolean onCollision(EnderPlayer withPlayer) {
		return false;
	}

	public void damage(float damage) {
		if (Float.isNaN(this.health))
			initHealth();
		if (health == 0)
			return;
		this.setHealth(Math.max(health - damage, 0));
		for (EnderPlayer p : Main.getInstance().onlinePlayers) {
			if (p.getLocation().isInRange(25, getLocation(), true)) {
				p.getNetworkManager().sendPacket(new PacketOutEntityStatus(getEntityId(), PacketOutEntityStatus.Status.LIVING_ENTITY_HURT));
				p.getNetworkManager().sendPacket(new PacketOutSoundEffect(isDead() ? getDeadSound() : getDamageSound(), location));
				p.getNetworkManager().sendPacket(new PacketOutAnimation(getEntityId(), (byte) 1));
			}
		}
	}

	public void damage(float damage, Vector knockback) {
		//damage delay
		if ((Main.getInstance().getCurrentServerTick() - this.latestDamage) < 4) {
			return;
		}
		this.latestDamage = Main.getInstance().getCurrentServerTick();
		
		this.damage(damage);
		this.getWorld().broadcastPacket(new PacketOutEntityVelocity(getEntityId(), knockback), this.getLocation());
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

	public final void setHealth(float health) {
		if (Float.isNaN(this.health))
			initHealth();
		if (health == this.health)
			return;
		if (health > this.maxHealth)
			throw new IllegalArgumentException("Health value too large");
		float lastHealth = this.health;
		this.health = health;
		onHealthUpdate(this.health, lastHealth);
	}

	protected abstract String getDamageSound();

	protected abstract String getDeadSound();

	protected abstract float getBaseHealth();

	protected abstract float getBaseMaxHealth();

	public abstract void updatePlayers(Set<EnderPlayer> onlinePlayers);

	public abstract void broadcastLocation(Location newLocation);

	public abstract void broadcastRotation(float pitch, float yaw);

	public abstract boolean isValid();

	public abstract void updateDataWatcher();

	public abstract void onSpawn();

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
	}
}
