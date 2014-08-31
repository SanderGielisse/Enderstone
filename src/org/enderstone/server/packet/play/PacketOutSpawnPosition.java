package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutSpawnPosition extends Packet {

	private int x;
	private int y;
	private int z;

	public PacketOutSpawnPosition() {
	}

	public PacketOutSpawnPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public int getSize() throws IOException {
		return (getIntSize() * 3) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x05;
	}
}
