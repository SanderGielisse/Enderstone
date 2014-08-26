package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInHeldItemChange extends Packet {

	private short slot;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.slot = buf.readShort();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getShortSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x09;
	}

	public short getSlot() {
		return slot;
	}
}
