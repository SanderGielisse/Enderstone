package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.entity.DataWatcher;
import org.enderstone.server.packet.Packet;

public class PacketOutEntityMetadata extends Packet {

	private int entityId;
	private DataWatcher dataWatcher;

	public PacketOutEntityMetadata(int entityId, DataWatcher dataWatcher) {
		this.entityId = entityId;
		this.dataWatcher = dataWatcher;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeVarInt(entityId, buf);
		writeDataWatcher(dataWatcher, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(entityId) + getDataWatcherSize(dataWatcher) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x1C;
	}
}
