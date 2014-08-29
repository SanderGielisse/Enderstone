package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketOutRespawn extends Packet {

	private int dimension;
	private byte difficulty;
	private byte gamemode;
	private String levelType;

	public PacketOutRespawn(int dimension, byte difficulty, byte gamemode, String levelType) {
		this.dimension = dimension;
		this.difficulty = difficulty;
		this.gamemode = gamemode;
		this.levelType = levelType;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeInt(dimension);
		buf.writeByte(difficulty);
		buf.writeByte(gamemode);
		writeString(levelType, buf);
	}

	@Override
	public int getSize() throws Exception {
		return getIntSize() + 2 + getStringSize(levelType) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x07;
	}
}
