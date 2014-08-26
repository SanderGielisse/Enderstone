package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.JSONStringBuilder;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInChatMessage extends Packet {

	private String jsonMessage;
	private String message;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.message = JSONStringBuilder.read(jsonMessage = readString(buf));
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(jsonMessage) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}

	public String getJSONMessage() {
		return jsonMessage;
	}

	public String getMessage() {
		return message;
	}
}
