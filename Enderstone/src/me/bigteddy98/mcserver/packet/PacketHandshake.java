package me.bigteddy98.mcserver.packet;

import io.netty.buffer.ByteBuf;

public class PacketHandshake extends Packet {

	private int protocol;
	private String hostname;
	private short port;
	private int nextState;

	public PacketHandshake(int protocol, String hostname, short port, int nextState) {
		this.protocol = protocol;
		this.hostname = hostname;
		this.port = port;
		this.nextState = nextState;
	}

	public PacketHandshake() {
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.protocol = readVarInt(buf);
		this.hostname = readString(buf);
		this.port = buf.readShort();
		this.nextState = readVarInt(buf);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		writeVarInt(protocol, buf);
		writeString(hostname, buf);
		buf.writeShort(port);
		writeVarInt(nextState, buf);
	}

	@Override
	public int getSize() throws Exception {
		return getVarIntSize(protocol) + getStringSize(hostname) + getShortSize() + getVarIntSize(nextState) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	public int getProtocol() {
		return protocol;
	}

	public String getHostname() {
		return hostname;
	}

	public short getPort() {
		return port;
	}

	public int getNextState() {
		return nextState;
	}
}
