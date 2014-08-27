package org.enderstone.server.packet.login;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketInLoginStart extends Packet {

	// incoming
	private String name;

	public PacketInLoginStart() {
	}

	public PacketInLoginStart(String name) {
		this.name = name;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.name = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(name) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	public String getPlayerName() {
		return name;
	}
}
