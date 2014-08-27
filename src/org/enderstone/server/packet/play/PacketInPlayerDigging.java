package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInPlayerDigging extends Packet {

	private byte status;
	private int x;
	private byte y;
	private int z;
	private byte face;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.status = buf.readByte();
		this.x = buf.readInt();
		this.y = buf.readByte();
		this.z = buf.readInt();
		this.face = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return 3 + (2 * getIntSize()) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x07;
	}

	public byte getStatus() {
		return status;
	}

	public int getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public byte getFace() {
		return face;
	}
}
