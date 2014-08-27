package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketInChatMessage extends Packet {

	private String message;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.message = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(message) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}

	public String getMessage() {
		return message;
	}
}
