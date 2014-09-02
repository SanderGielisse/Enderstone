package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Location;
import org.enderstone.server.packet.Packet;

public class PacketOutSpawnPosition extends Packet {

	private Location loc;

	public PacketOutSpawnPosition(Location loc) {
		this.loc = loc;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeLocation(loc, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getLocationSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x05;
	}
}
