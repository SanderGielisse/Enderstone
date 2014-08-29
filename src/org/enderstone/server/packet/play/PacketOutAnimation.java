package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketOutAnimation extends Packet {

	private int entityId;
	private byte animation;

	public PacketOutAnimation(int entityId, byte animation) {
		this.entityId = entityId;
		this.animation = animation;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		writeVarInt(entityId, buf);
		buf.writeByte(animation);
	}

	@Override
	public int getSize() throws Exception {
		return getVarIntSize(entityId) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0B;
	}
}
