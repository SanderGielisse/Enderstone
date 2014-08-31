package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Main;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInPlayerOnGround extends Packet {

	private boolean onGround;

	public PacketInPlayerOnGround() {
	}

	public PacketInPlayerOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.onGround = buf.readBoolean();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x03;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				if (networkManager.player.waitingForValidMoveAfterTeleport > 0) {
					return;
				}
				networkManager.player.setOnGround(isOnGround());
			}
		});
	}

	public boolean isOnGround() {
		return onGround;
	}
}
