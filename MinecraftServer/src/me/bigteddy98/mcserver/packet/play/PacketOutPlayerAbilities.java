package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketOutPlayerAbilities extends Packet {

	private byte flags;
	private float flySpeed;
	private float walkSpeed;

	public PacketOutPlayerAbilities() {
	}

	public PacketOutPlayerAbilities(byte flags, float flySpeed, float walkSpeed) {
		this.flags = flags;
		this.flySpeed = flySpeed;
		this.walkSpeed = walkSpeed;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeByte(flags);
		buf.writeFloat(flySpeed);
		buf.writeFloat(walkSpeed);
	}

	@Override
	public int getSize() throws Exception {
		return 1 + (getFloatSize() * 2) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x39;
	}
}
