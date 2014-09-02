package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Location;
import org.enderstone.server.entity.GameMode;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInClientStatus extends Packet {

	private int actionId;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.actionId = buf.readUnsignedByte();
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
		return 0x16;
	}

	@Override
	public void onRecieve(NetworkManager networkManager) {
		// EnderLogger.warn("Client status: " + getActionId());

		if (getActionId() == 0) {
			networkManager.sendPacket(new PacketOutRespawn(0, (byte) 0, (byte) GameMode.SURVIVAL.getId(), "default"));
			networkManager.player.teleport(new Location("", 0, 80, 0, 0F, 0F));
			networkManager.player.heal();
		}
	}

	public int getActionId() {
		return actionId;
	}
}
