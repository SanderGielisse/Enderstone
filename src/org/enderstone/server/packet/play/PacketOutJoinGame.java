package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutJoinGame extends Packet {

	private int entityId;
	private byte gamemode;
	private byte dimension;
	private byte difficulty;
	private byte maxPlayers;
	private String levelType;
	private boolean reducedDebugInfo;

	public PacketOutJoinGame(int entityId, byte gamemode, byte dimension, byte difficulty, byte maxPlayers, String levelType, boolean reducedDebugInfo) {
		this.entityId = entityId;
		this.gamemode = gamemode;
		this.dimension = dimension;
		this.difficulty = difficulty;
		this.maxPlayers = maxPlayers;
		this.levelType = levelType;
		this.reducedDebugInfo = reducedDebugInfo;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(entityId);
		buf.writeByte(gamemode);
		buf.writeByte(dimension);
		buf.writeByte(difficulty);
		buf.writeByte(maxPlayers);
		writeString(levelType, buf);
		buf.writeBoolean(reducedDebugInfo);
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 5 + getStringSize(levelType) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}
}
