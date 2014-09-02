package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketInAnimation extends Packet {

	private int entityId;
	private byte animation;

	public PacketInAnimation() {
		//TODO protocol website UNKNOWN
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.entityId = buf.readInt();
		this.animation = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0A;
	}

	public int getEntityId() {
		return entityId;
	}

	public byte getAnimation() {
		return animation;
	}
}
