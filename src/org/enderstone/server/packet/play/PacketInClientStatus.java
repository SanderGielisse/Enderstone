package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInClientStatus extends Packet {

	private byte actionId;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.actionId = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x16;
	}

	public byte getActionId() {
		return actionId;
	}
}
