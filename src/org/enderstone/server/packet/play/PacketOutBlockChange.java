package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutBlockChange extends Packet {

	private int x;
	private int y;
	private int z;
	private int blockId;
	private byte metadata;

	public PacketOutBlockChange(int x, int y, int z, int blockId, byte metadata) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockId = blockId;
		this.metadata = metadata;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(x);
		buf.writeByte(y);
		buf.writeInt(z);
		writeVarInt(blockId, buf);
		buf.writeByte(metadata);
	}

	@Override
	public int getSize() throws IOException {
		return (getIntSize() * 2) + 2 + getVarIntSize(blockId) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x23;
	}
}
