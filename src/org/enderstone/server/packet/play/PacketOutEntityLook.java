package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutEntityLook extends Packet {

	private int entityId;
	private byte yaw;
	private byte pitch;

	public PacketOutEntityLook(int entityId, byte yaw, byte pitch) {
		this.entityId = entityId;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(entityId);
		buf.writeByte(yaw);
		buf.writeByte(pitch);
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 2 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x16;
	}
}
