package org.enderstone.server.packet.play;

import org.enderstone.server.EnderLogger;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInPlayerPosition extends Packet {

	private double x;
	private double feetY;
	private double headY;
	private double z;
	private boolean onGround;

	public PacketInPlayerPosition() {
	}

	public PacketInPlayerPosition(double x, double feetY, double headY, double z, boolean onGround) {
		this.x = x;
		this.feetY = feetY;
		this.headY = headY;
		this.z = z;
		this.onGround = onGround;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.x = buf.readDouble();
		this.feetY = buf.readDouble();
		this.headY = buf.readDouble();
		this.z = buf.readDouble();
		this.onGround = buf.readBoolean();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return (getDoubleSize() * 4) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x04;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				if (networkManager.player != null) {
					Location loc = networkManager.player.getLocation();
					if (networkManager.player.waitingForValidMoveAfterTeleport > 0) {
						if (Math.max(Math.max(
								getX() < loc.getX() ? loc.getX() - getX() : getX() - loc.getX(),
								getHeadY() < loc.getY() ? loc.getY() - getHeadY() : getHeadY() - loc.getY()),
								getZ() < loc.getZ() ? loc.getZ() - getZ() : getZ() - loc.getZ()
						) > 0.1) {
							if (networkManager.player.waitingForValidMoveAfterTeleport++ > 100) {
								networkManager.player.teleport(loc);
							}
							return;
						}
						networkManager.player.waitingForValidMoveAfterTeleport = 0;
					}
					networkManager.player.broadcastLocation(new Location("", getX(), getFeetY(), getZ(), networkManager.player.getLocation().getYaw(), networkManager.player.getLocation().getPitch()));
					loc.setX(getX());
					loc.setY(getFeetY());
					loc.setZ(getZ());
					networkManager.player.setOnGround(isOnGround());
				}
			}
		});
	}

	public double getX() {
		return x;
	}

	public double getFeetY() {
		return feetY;
	}

	public double getHeadY() {
		return headY;
	}

	public double getZ() {
		return z;
	}

	public boolean isOnGround() {
		return onGround;
	}
}
