package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Location;
import org.enderstone.server.packet.Packet;

public class PacketOutBlockChange extends Packet {

	private Location loc;
	private int blockIdData;

	public PacketOutBlockChange(Location loc, int blockId, byte dataValue) {
		this.loc = loc;
		this.blockIdData = (blockId << 4) | dataValue;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeLocation(loc, buf);
		writeVarInt(blockIdData, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getLocationSize() + getVarIntSize(blockIdData) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x23;
	}
}
