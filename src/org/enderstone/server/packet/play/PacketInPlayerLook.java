package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInPlayerLook extends Packet {

	private float yaw;
	private float pitch;
	private boolean onGround;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();
		this.onGround = buf.readBoolean();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return (getFloatSize() * 2) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x05;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				Location loc = networkManager.player.getLocation();

				if (networkManager.player.waitingForValidMoveAfterTeleport > 0) {
					return;
				}

				try {
					networkManager.player.broadcastRotation(getPitch(), getYaw());
				} catch (Exception e) {
					e.printStackTrace();
				}
				loc.setPitch(getPitch());
				loc.setYaw(getYaw());
				networkManager.player.setOnGround(isOnGround());
			}
		});
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
