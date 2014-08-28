package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;

import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

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

	@Override
	public void onRecieve(NetworkManager networkManager) throws Exception {
		if (getChannel().equals("REGISTER")) {
			// REGISTER.add(new String(message.getData(), "UTF-8"));
		} else if (getChannel().equals("UNREGISTER")) {
			// REGISTER.remove(new String(message.getData(), "UTF-8"));
		} else if (getChannel().equals("MC|Brand")) {
			networkManager.sendPacket(new PacketOutPluginMessage(getChannel(), getLength(), getData()));
		}
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
