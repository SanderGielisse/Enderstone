package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketInHeldItemChange extends Packet {

	private short slot;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.slot = buf.readShort();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
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
