package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketInEntityAction extends Packet {

	private int entityId;
	private byte actionId;
	private int jumpBoost;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.entityId = buf.readInt();
		this.actionId = buf.readByte();
		this.jumpBoost = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return (getIntSize() * 2) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0B;
	}

	public int getEntityId() {
		return entityId;
	}

	public byte getActionId() {
		return actionId;
	}

	public int getJumpBoost() {
		return jumpBoost;
	}
}
