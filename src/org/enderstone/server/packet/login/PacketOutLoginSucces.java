package org.enderstone.server.packet.login;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketOutLoginSucces extends Packet {

	private String UUID;
	private String username;

	public PacketOutLoginSucces() {
	}

	public PacketOutLoginSucces(String UUID, String username) {
		this.UUID = UUID;
		this.username = username;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.UUID = readString(buf);
		this.username = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		writeString(UUID, buf);
		writeString(username, buf);
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(username) + getStringSize(UUID) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x02;
	}

	public String getUUID() {
		return UUID;
	}

	public String getUsername() {
		return username;
	}
}
