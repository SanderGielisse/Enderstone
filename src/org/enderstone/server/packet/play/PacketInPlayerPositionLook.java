package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;

import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInPlayerPositionLook extends Packet {

	private double x;
	private double feetY;
	private double headY;
	private double z;
	private float yaw;
	private float pitch;
	private boolean onGround;

	public PacketInPlayerPositionLook() {
	}

	public PacketInPlayerPositionLook(double x, double feetY, double headY, double z, float yaw, float pitch, boolean onGround) {
		this.x = x;
		this.feetY = feetY;
		this.headY = headY;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.x = buf.readDouble();
		this.feetY = buf.readDouble();
		this.headY = buf.readDouble();
		this.z = buf.readDouble();
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();
		this.onGround = buf.readBoolean();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return (getDoubleSize() * 4) + (getFloatSize() * 2) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x06;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) throws Exception {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				Location loc = networkManager.player.getLocation();
				try {
					networkManager.player.broadcastLocation(new Location("", getX(), getFeetY(), getZ(), getYaw(), getPitch()));
					networkManager.player.broadcastRotation(getPitch(), getYaw());
				} catch (Exception e) {
					e.printStackTrace();
				}
				loc.setX(getX());
				loc.setY(getFeetY());
				loc.setZ(getZ());
				loc.setPitch(getPitch());
				loc.setYaw(getYaw());
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

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public boolean isOnGround() {
		return onGround;
	}
}
