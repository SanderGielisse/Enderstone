package org.enderstone.server.packet.play;

import java.io.IOException;
import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketOutSpawnObject extends Packet {

	private int entityId;
	private byte entityType;
	private int x;
	private int y;
	private int z;
	private byte pitch;
	private byte yaw;

	private int dataSize;
	private short dataOne;
	private short dataTwo;
	private short dataThree;

	public PacketOutSpawnObject(int entityId, byte entityType, int x, int y, int z, byte pitch, byte yaw, int dataSize, short dataOne, short dataTwo, short dataThree) {
		this.entityId = entityId;
		this.entityType = entityType;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.dataSize = dataSize;
		this.dataOne = dataOne;
		this.dataTwo = dataTwo;
		this.dataThree = dataThree;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeVarInt(entityId, buf);
		buf.writeByte(entityType);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(pitch);
		buf.writeByte(yaw);

		buf.writeInt(dataSize);
		if (dataSize > 0) {
			buf.writeShort(dataOne);
			buf.writeShort(dataTwo);
			buf.writeShort(dataThree);
		}
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(entityId) + 3 + (getIntSize() * 4) + ((dataSize > 0) ? (getShortSize() * 3) : 0) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0E;
	}
}
