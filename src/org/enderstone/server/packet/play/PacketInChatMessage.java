package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInChatMessage extends Packet {

	private String message;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.message = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(message) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
				networkManager.player.onPlayerChat(getMessage());
	}

	public String getMessage() {
		return message;
	}
}
