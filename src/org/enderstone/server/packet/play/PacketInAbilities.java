package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInAbilities extends Packet {

	private byte flags;
	private float flySpeed;
	private float walkingSpeed;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.flags = buf.readByte();
		this.flySpeed = buf.readFloat();
		this.walkingSpeed = buf.readFloat();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return 1 + (getFloatSize() * 2) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x13;
	}

	public byte getFlags() {
		return flags;
	}

	public float getFlySpeed() {
		return flySpeed;
	}

	public float getWalkingSpeed() {
		return walkingSpeed;
	}
}
