package org.enderstone.server.entity;

import org.enderstone.server.Location;
import org.enderstone.server.packet.Packet;

public class Entity {

	private static int entityCount = 1;

	private final int entityId;
	private final int appearanceId;
	private final Location location;

	public Entity(int appearanceId, Location location) {
		this.entityId = entityCount++;
		this.appearanceId = appearanceId;
		this.location = location;
	}

	public int getEntityId() {
		return entityId;
	}

	public int getAppearanceId() {
		return appearanceId;
	}

	public Location getLocation() {
		return location;
	}

	public Packet getSpawnPacket() {
		return null; //TODO
	}
}
