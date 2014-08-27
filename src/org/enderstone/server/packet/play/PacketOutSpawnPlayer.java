package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.enderstone.server.entity.DataWatcher;
import org.enderstone.server.entity.ProfileProperty;
import org.enderstone.server.packet.Packet;

public class PacketOutSpawnPlayer extends Packet {

	private int entityId;
	private String uuid;
	private String name;

	private int dataCount;
	private List<ProfileProperty> data;

	private int x;
	private int y;
	private int z;
	private byte yaw;
	private byte pitch;

	private short currentSlot;
	private DataWatcher dataWatcher;

	public PacketOutSpawnPlayer(int entityId, String uuid, String name, List<ProfileProperty> data, int x, int y, int z, byte yaw, byte pitch, short currentSlot, DataWatcher dataWatcher) {
		this.entityId = entityId;
		this.uuid = uuid;
		this.name = name;
		this.dataCount = data.size();
		this.data = data;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.currentSlot = currentSlot;
		this.dataWatcher = dataWatcher;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		writeVarInt(entityId, buf);
		writeString(uuid, buf);
		writeString(name, buf);
		
		writeVarInt(dataCount, buf);

		for (ProfileProperty prop : data) {
			writeString(prop.getName(), buf);
			writeString(prop.getValue(), buf);
			writeString(prop.getSignature(), buf);
		}

		buf.writeInt((int) (x * 32.0D));
		buf.writeInt((int) (y * 32.0D));
		buf.writeInt((int) (z * 32.0D));
		buf.writeByte(yaw);
		buf.writeByte(pitch);

		buf.writeShort(currentSlot);
		writeDataWatcher(dataWatcher, buf);
	}

	@Override
	public int getSize() throws Exception {

		int sizee = 0;

		for (ProfileProperty prop : data) {
			sizee += (getStringSize(prop.getName()) + getStringSize(prop.getValue()) + getStringSize(prop.getSignature()));
		}
		return getVarIntSize(entityId) + getStringSize(uuid) + getStringSize(name) + getVarIntSize(dataCount) + sizee + (getIntSize() * 3) + 2 + getShortSize() + getDataWatcherSize(dataWatcher) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0C;
	}
}
