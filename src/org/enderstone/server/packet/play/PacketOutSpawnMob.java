package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.entity.DataWatcher;
import org.enderstone.server.packet.Packet;

public class PacketOutSpawnMob extends Packet {

	private int entityId;
	private byte type;
	private int x;
	private int y;
	private int z;
	private byte yaw;
	private byte pitch;
	private byte headPitch;
	private short velocityX;
	private short velocityY;
	private short velocityZ;
	private DataWatcher dataWatcher;
	
	public PacketOutSpawnMob(int entityId, byte type, int x, int y, int z, byte yaw, byte pitch, byte headPitch, short velocityX, short velocityY, short velocityZ, DataWatcher dataWatcher) {
		this.entityId = entityId;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.headPitch = headPitch;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.dataWatcher = dataWatcher;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeVarInt(entityId, buf);
		buf.writeByte(type);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(yaw);
		buf.writeByte(pitch);
		buf.writeByte(headPitch);
		buf.writeShort(velocityX);
		buf.writeShort(velocityY);
		buf.writeShort(velocityZ);
		writeDataWatcher(dataWatcher, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(entityId) + 4 + (getIntSize() * 3) + (getShortSize() * 3) + getDataWatcherSize(dataWatcher) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0F;
	}
}
