package me.bigteddy98.mcserver.entity;

import me.bigteddy98.mcserver.packet.NetworkManager;
import me.bigteddy98.mcserver.regions.EnderChunk;
import me.bigteddy98.mcserver.regions.EnderWorld.ChunkInformer;

public class EnderPlayer extends Entity {

	private final NetworkManager networkManager;;
	private final String playerName;
	private String locale;
	private byte renderDistance;
	private byte chatFlags;
	private boolean chatColors;
	private byte difficulty;
	private boolean showCapes;
	public int keepAliveID = 0;

	public ChunkInformer chunkInformer = new ChunkInformer() {

		@Override
		public boolean sendChunk(EnderChunk chunk) throws Exception {
			networkManager.sendPacket(chunk.getCompressedChunk().toPacket(chunk.getX(), chunk.getZ()));
			return true;
		}

		@Override
		public boolean removeChunk(EnderChunk chunk) {
			return true;
		}
	};

	public EnderPlayer(String name, NetworkManager networkManager) {
		super(-1, new Location());
		this.networkManager = networkManager;
		this.playerName = name;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public byte getRenderDistance() {
		return renderDistance;
	}

	public void setRenderDistance(byte renderDistance) {
		this.renderDistance = renderDistance;
	}

	public byte getChatFlags() {
		return chatFlags;
	}

	public void setChatFlags(byte chatFlags) {
		this.chatFlags = chatFlags;
	}

	public boolean hasChatColors() {
		return chatColors;
	}

	public void setChatColors(boolean chatColors) {
		this.chatColors = chatColors;
	}

	public byte getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(byte difficulty) {
		this.difficulty = difficulty;
	}

	public boolean getShowCapes() {
		return showCapes;
	}

	public void setShowCapes(boolean showCapes) {
		this.showCapes = showCapes;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public String getPlayerName() {
		return playerName;
	}
}
