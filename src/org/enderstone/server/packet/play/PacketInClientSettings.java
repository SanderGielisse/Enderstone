package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInClientSettings extends Packet {

	private String locale;
	private byte renderDistance;
	private byte chatFlags;
	private boolean chatColors;
	private byte difficulty;
	private boolean showCapes;

	public PacketInClientSettings(String locale, byte renderDistance, byte chatFlags, boolean chatColors, byte difficulty, boolean showCapes) {
		this.locale = locale;
		this.renderDistance = renderDistance;
		this.chatFlags = chatFlags;
		this.chatColors = chatColors;
		this.difficulty = difficulty;
		this.showCapes = showCapes;
	}

	public PacketInClientSettings() {
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.locale = readString(buf);		
		this.renderDistance = buf.readByte();
		this.chatFlags = buf.readByte();
		this.chatColors = buf.readBoolean();
		this.difficulty = buf.readByte();
		this.showCapes = buf.readBoolean();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(locale) + 5 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x15;
	}

	public String getLocale() {
		return locale;
	}

	public byte getRenderDistance() {
		return renderDistance;
	}

	public byte getChatFlags() {
		return chatFlags;
	}

	public boolean getChatColors() {
		return chatColors;
	}

	public byte getDifficulty() {
		return difficulty;
	}

	public boolean getShowCapes() {
		return showCapes;
	}
}
