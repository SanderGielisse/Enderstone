package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

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
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public int getSize() throws Exception {
		return (getIntSize() * 3) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x05;
	}
}
