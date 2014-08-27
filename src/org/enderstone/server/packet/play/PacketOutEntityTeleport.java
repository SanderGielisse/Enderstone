package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;

import org.enderstone.server.packet.Packet;

public class PacketOutEntityTeleport extends Packet {

	private int entityId;
	private int x;
	private int y;
	private int z;
	private byte yaw;
	private byte pitch;

	public PacketOutEntityTeleport(int entityId, int x, int y, int z, byte yaw, byte pitch) {
		this.entityId = entityId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeInt(entityId);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(yaw);
		buf.writeByte(pitch);
	}

	@Override
	public int getSize() throws Exception {
		return (getIntSize() * 4) + 2 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x18;
	}
}