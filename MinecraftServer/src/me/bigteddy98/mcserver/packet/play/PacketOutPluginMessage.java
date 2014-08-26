package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketOutPluginMessage extends Packet {

	private String channel;
	private short length;
	private byte[] data;

	public PacketOutPluginMessage() {
	}

	public PacketOutPluginMessage(String channel, short length, byte[] data) {
		this.channel = channel;
		this.length = length;
		this.data = data;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		writeString(channel, buf);
		buf.writeShort(length);
		buf.writeBytes(data);
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(channel) + getShortSize() + data.length + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x3F;
	}

	public String getChannel() {
		return channel;
	}

	public short getLength() {
		return length;
	}

	public byte[] getData() {
		return data;
	}
}
