package org.enderstone.server.entity;

import java.util.List;
import org.enderstone.server.Location;
import org.enderstone.server.packet.Packet;

public abstract class Entity {

	private static int entityCount = 0;

	private final int entityId;
	private final Location location;

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

	public abstract void onRightClick();

	public abstract void onLeftClick();

	public abstract void damage(float damage);

	public abstract void onDeath();

	public abstract void updatePlayers(List<EnderPlayer> onlinePlayers);

	public abstract void broadcastLocation(Location newLocation);

	public abstract void broadcastRotation(float pitch, float yaw);
	
	public abstract boolean isValid();
}
