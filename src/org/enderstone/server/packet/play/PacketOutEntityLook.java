package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutEntityLook extends Packet {

	private int entityId;
	private byte yaw;
	private byte pitch;
	private boolean onGround;

	public PacketOutEntityLook(int entityId, byte yaw, byte pitch, boolean onGround) {
		this.entityId = entityId;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeVarInt(entityId, buf);
		buf.writeByte(yaw);
		buf.writeByte(pitch);
		buf.writeBoolean(onGround);
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(entityId) + 3 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x16;
	}
}
