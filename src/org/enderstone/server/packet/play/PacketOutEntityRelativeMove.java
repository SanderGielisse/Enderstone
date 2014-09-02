package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutEntityRelativeMove extends Packet {

	private int entityId;
	private byte dX;
	private byte dY;
	private byte dZ;
	private boolean onGround;

	public PacketOutEntityRelativeMove(int entityId, byte dX, byte dY, byte dZ, boolean onGround) {
		this.entityId = entityId;
		this.dX = dX;
		this.dY = dY;
		this.dZ = dZ;
		this.onGround = onGround;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(entityId);
		buf.writeByte(dX);
		buf.writeByte(dY);
		buf.writeByte(dZ);
		buf.writeBoolean(onGround);
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 4 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x15;
	}
}
