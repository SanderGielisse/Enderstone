package org.enderstone.server.entity;

import java.util.List;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityMetadata;
import org.enderstone.server.packet.play.PacketOutSpawnObject;

public class EntityItem extends Entity {

	private final ItemStack itemstack;

	public EntityItem(Location location, ItemStack stack) {
		super(location);
		this.itemstack = stack;
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
		return new PacketOutSpawnObject(getEntityId(), (byte) 2, (int) ((loc.getX() + 0.5) * 32.0D), (int) ((loc.getY() + 0.5) * 32.0D), (int) ((loc.getZ() + 0.5) * 32.0D), (byte) 0, (byte) 0, 0, (short) 2, (short) 2, (short) 2);
	}

	@Override
	public void teleport(Location loc) {
	}

	@Override
	public void teleport(Entity entity) {
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
	public void updatePlayers(List<EnderPlayer> onlinePlayers) {
		Packet spawnPacket = this.getSpawnPacket();
		Packet packet = new PacketOutEntityMetadata(this.getEntityId(), this.getDataWatcher());
		for (EnderPlayer pl : onlinePlayers) {
			if (pl.getLocation().isInRange(40, getLocation()) && !pl.canSeeEntity.contains(this)) {
				pl.canSeeEntity.add(this);
				pl.getNetworkManager().sendPacket(spawnPacket);
				pl.getNetworkManager().sendPacket(packet);
				EnderLogger.warn("Sent to " + pl.getPlayerName());
			} else if (!pl.getLocation().isInRange(40, this.getLocation()) && pl.canSeeEntity.contains(this)) {
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
		this.getDataWatcher().watch(0, (byte)0);
		this.getDataWatcher().watch(1, (short) 300);
		this.getDataWatcher().watch(10, this.itemstack);
	}
}
