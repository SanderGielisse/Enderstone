package me.bigteddy98.mcserver.packet.login;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

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
		System.out.println("Name: " + name);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		writeString(name, buf);
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
