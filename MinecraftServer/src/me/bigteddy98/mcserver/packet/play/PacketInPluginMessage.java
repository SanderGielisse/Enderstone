package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInPluginMessage extends Packet {

	private String channel;
	private short length;
	private byte[] data;

	public PacketInPluginMessage() {
	}

	public PacketInPluginMessage(String channel, short length, byte[] data) {
		this.channel = channel;
		this.length = length;
		this.data = data;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.channel = readString(buf);
		this.length = buf.readShort();
		this.data = new byte[length];
		buf.readBytes(this.data, 0, this.length);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(channel) + getShortSize() + data.length + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x17;
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
