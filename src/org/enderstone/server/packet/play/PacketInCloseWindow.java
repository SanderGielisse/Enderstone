package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketInCloseWindow extends Packet {

	private byte windowId;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.windowId = buf.readByte();
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
		return 0x0D;
	}

	public byte getWindowId() {
		return windowId;
	}
}
