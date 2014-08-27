package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInUseEntity extends Packet {

	private int targetId;
	private byte mouseClick;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.targetId = buf.readInt();
		this.mouseClick = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getIntSize() + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x02;
	}

	public int getTargetId() {
		return targetId;
	}

	public byte getMouseClick() {
		return mouseClick;
	}
}
