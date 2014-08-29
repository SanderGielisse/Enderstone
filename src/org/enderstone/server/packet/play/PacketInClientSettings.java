package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

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

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable(){

			@Override
			public void run() {
				EnderPlayer player;
				if((player=networkManager.player) == null) return;
				player.clientSettings.setChatColors(chatColors);
				player.clientSettings.setChatFlags(chatFlags);
				player.clientSettings.setDifficulty(difficulty);
				player.clientSettings.setLocale(locale);
				player.clientSettings.setRenderDistance(renderDistance);
				player.clientSettings.setShowCapes(showCapes);
			}
		});
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
