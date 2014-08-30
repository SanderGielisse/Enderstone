package org.enderstone.server.entity;

import java.util.List;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutAnimation;
import org.enderstone.server.packet.play.PacketOutEntityStatus;
import org.enderstone.server.packet.play.PacketOutSoundEffect;

public abstract class Entity {

	private static int entityCount = 0;

	private final int entityId;
	private final Location location;
	private float health = Float.NaN;
	private float maxHealth = Float.NaN;

	public Entity(Location location) {
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

	public abstract void teleport(Entity entity);

	public abstract void onRightClick(EnderPlayer attacker);

	public abstract void onLeftClick(EnderPlayer attacker);

	public void damage(float damage) {
		if(Float.isNaN(this.health)) initHealth();
		if (health == 0) return;
		this.setHealth(Math.max(health - damage, 0));
		for (EnderPlayer p : Main.getInstance().onlinePlayers) {
			if (p.getLocation().isInRange(25, getLocation())) {
				p.getNetworkManager().sendPacket(new PacketOutEntityStatus(getEntityId(), PacketOutEntityStatus.Status.LIVING_ENTITY_HURT));
				p.getNetworkManager().sendPacket(new PacketOutSoundEffect(isDead() ? getDeadSound() : getDamageSound(), location));
				p.getNetworkManager().sendPacket(new PacketOutAnimation(getEntityId(), (byte) 1));
			}
		}
	}

	public final boolean isDead() {
		if(Float.isNaN(this.health)) initHealth();
		return this.health <= 0;
	}
	
	public final void kill()
	{
		damage(1000);
	}
	
	public final void heal()
	{
		setHealth(this.getMaxHealth());
	}

	protected void onHealthUpdate(float newHealth, float lastHealth) {
	}

	public float getHealth() {
		if(Float.isNaN(this.health)) initHealth();
		return health;
	}

	public final float getMaxHealth() {
		if(Float.isNaN(this.health)) initHealth();
		return maxHealth;
	}

	public final void setMaxHealth(float health) {
		if(Float.isNaN(this.health)) initHealth();
		setHealth(Math.min(health, this.health));
		this.maxHealth = health;
	}

	public final void setHealth(float health) {
		if(Float.isNaN(this.health)) initHealth();
		if (health == this.health) return;
		if (health > this.maxHealth) throw new IllegalArgumentException("Health value too large");
		float lastHealth = this.health;
		this.health = health;
		onHealthUpdate(this.health,lastHealth);
	}
	
	protected abstract String getDamageSound();
	
	protected abstract String getDeadSound();
	
	protected abstract float getBaseHealth();
	
	protected abstract float getBaseMaxHealth();

	public abstract void updatePlayers(List<EnderPlayer> onlinePlayers);

	public abstract void broadcastLocation(Location newLocation);

	public abstract void broadcastRotation(float pitch, float yaw);

	public abstract boolean isValid();

	private void initHealth() {
		this.health = getBaseHealth();
		this.maxHealth = getBaseMaxHealth();
	}
}
