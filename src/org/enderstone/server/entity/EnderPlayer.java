package org.enderstone.server.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.Utill;
import org.enderstone.server.chat.ChatColor;
import org.enderstone.server.chat.Message;
import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.inventory.Inventory;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketInTabComplete;
import org.enderstone.server.packet.play.PacketOutChatMessage;
import org.enderstone.server.packet.play.PacketOutChunkData;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityHeadLook;
import org.enderstone.server.packet.play.PacketOutEntityLook;
import org.enderstone.server.packet.play.PacketOutEntityRelativeMove;
import org.enderstone.server.packet.play.PacketOutEntityTeleport;
import org.enderstone.server.packet.play.PacketOutPlayerListItem;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.Action;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.ActionAddPlayer;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.ActionRemovePlayer;
import org.enderstone.server.packet.play.PacketOutPlayerPositionLook;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.packet.play.PacketOutSpawnPlayer;
import org.enderstone.server.packet.play.PacketOutTabComplete;
import org.enderstone.server.packet.play.PacketOutUpdateHealth;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld.ChunkInformer;

public class EnderPlayer extends Entity implements CommandSender {

	private static final int MAX_CHUNKS_EVERY_UPDATE = 16;

	public final ClientSettings clientSettings = new ClientSettings();
	public final NetworkManager networkManager;
	public final String playerName;
	public HashSet<String> visiblePlayers = new HashSet<>();
	public HashSet<Entity> canSeeEntity = new HashSet<>();
	/**
	 * If this is above 0, then the server is waiting for a correction on the
	 * last teleport the server sended
	 */
	public int waitingForValidMoveAfterTeleport = 0;
	public final UUID uuid;
	public volatile boolean isOnline = true;
	public boolean isCreative = false;
	public boolean godMode = false;
	public boolean canFly = false;
	public boolean isFlying = false;
	public boolean isOnFire = false;
	public boolean isSneaking = false;
	public boolean isSprinting = false;
	public boolean isEating = false;
	private boolean isInvisible = false;

	public volatile boolean isOnGround = true;
	public double yLocation;
	public short food = 20;
	public float foodSaturation = 0;
	public Inventory inventory;

	private final String textureValue;
	private final String textureSignature;
	public int keepAliveID = 0;

	public ChunkInformer chunkInformer = new ChunkInformer() {

		List<EnderChunk> cache = new ArrayList<>();

		@Override
		public void sendChunk(EnderChunk chunk) {
			cache.add(chunk);
		}

		@Override
		public void removeChunk(EnderChunk chunk) {
			networkManager.sendPacket(PacketOutChunkData.clearChunk(chunk.getX(), chunk.getZ()));
		}

		@Override
		public void done() {
			int size = cache.size();
			if (size == 0)
				return;
			Packet[] packets = new Packet[size];
			for (int i = 0; i < size; i++) {
				EnderChunk c = cache.get(i);
				packets[i] = c.getCompressedChunk().toPacket(c.getX(), c.getZ());
			}
			cache.clear();
			networkManager.sendPacket(packets);
		}

		@Override
		public int maxChunks() {
			return MAX_CHUNKS_EVERY_UPDATE;
		}
	};

	public EnderPlayer(String userName, NetworkManager networkManager, UUID uuid, PlayerTextureStore textures) {
		super(new Location());
		this.networkManager = networkManager;
		this.playerName = userName;
		this.uuid = uuid;
		this.textureValue = textures.getSkin().value;
		this.textureSignature = textures.getSkin().signature;
		EnderLogger.info(userName + " logged in with uuid " + uuid);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnderPlayer other = (EnderPlayer) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public String getPlayerName() {
		return playerName;
	}

	public ProfileProperty getTextureProperty() {
		return new ProfileProperty("textures", textureValue, true, textureSignature);
	}

	public ProfileProperty[] getProfileProperties() {
		ProfileProperty[] list = new ProfileProperty[1];
		list[0] = getTextureProperty();
		return list;
	}

	@Override
	public void onSpawn() {
		this.updateDataWatcher();

		PacketOutPlayerListItem packet = new PacketOutPlayerListItem(new Action[] { new ActionAddPlayer(this.uuid, this.getPlayerName(), getProfileProperties(), GameMode.SURVIVAL.getId(), 1, false, "") });
		for (EnderPlayer player : Main.getInstance().onlinePlayers) {
			player.getNetworkManager().sendPacket(packet);
			this.getNetworkManager().sendPacket(new PacketOutPlayerListItem(new Action[] { new ActionAddPlayer(player.uuid, player.getPlayerName(), player.getProfileProperties(), GameMode.SURVIVAL.getId(), 1, false, "") }));
		}
		Main.getInstance().broadcastMessage(new SimpleMessage(ChatColor.YELLOW + this.getPlayerName() + " joined the game!"));
		this.inventory = new Inventory(this);
	}

	@Override
	public void updateDataWatcher() {
		int meaning = 0;

		if (isOnFire)
			meaning = (byte) (meaning | 0x01);
		if (isSneaking)
			meaning = (byte) (meaning | 0x02);
		if (isSprinting)
			meaning = (byte) (meaning | 0x08);
		if (isEating)
			meaning = (byte) (meaning | 0x10);
		if (isInvisible)
			meaning = (byte) (meaning | 0x20);

		this.getDataWatcher().watch(0, (byte) meaning);
		this.getDataWatcher().watch(1, (short) 0);
		this.getDataWatcher().watch(6, 1F);
		this.getDataWatcher().watch(8, (byte) 0);
	}

	@Override
	public Packet getSpawnPacket() {
		return new PacketOutSpawnPlayer(this.getEntityId(), this.uuid, (int) (this.getLocation().getX() * 32.0D), (int) (this.getLocation().getY() * 32.0D), (int) (this.getLocation().getZ() * 32.0D), (byte) 0, (byte) 0, (short) 0, this.getDataWatcher());
	}

	public void onPlayerChat(final String message) {
		if (message.startsWith("/")) {
			final String fullCommand = message.substring(1);
			final String[] split = fullCommand.split(" ");
			final String[] args;
			if (split.length != 1) {
				args = new String[split.length - 1];
				System.arraycopy(split, 1, args, 0, args.length);
			} else
				args = new String[0];

			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {

					Main.getInstance().commands.executeCommand(null, split[0], EnderPlayer.this, args);
				}
			});
		} else {
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					Utill.broadcastMessage("<" + getPlayerName() + "> " + message);
				}
			});
		}
	}

	public void onDisconnect() {
		this.isOnline = false;
		Utill.broadcastMessage(ChatColor.YELLOW + this.getPlayerName() + " left the game!");
		Main.getInstance().mainWorld.players.remove(this);

		for (EnderPlayer p : Main.getInstance().onlinePlayers) {
			p.getNetworkManager().sendPacket(new PacketOutPlayerListItem(new Action[] { new ActionRemovePlayer(this.uuid) }));
		}
		if (Main.getInstance().onlinePlayers.contains(this)) {
			Main.getInstance().onlinePlayers.remove(this);
			for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
				for (String name : ep.visiblePlayers) {
					if (name.equals(this.getPlayerName()) && !this.getPlayerName().equals(ep.getPlayerName())) {
						ep.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[]{this.getEntityId()}));
					}
				}
			}
		}
		Main.getInstance().broadcastMessage(new SimpleMessage(ChatColor.YELLOW + playerName + " left the game!"));
	}

	@Override
	public boolean isOnline() {
		return this.isOnline;
	}

	@Override
	public void updatePlayers(List<EnderPlayer> onlinePlayers) {
		Set<Integer> toDespawn = new HashSet<>();
		for (EnderPlayer pl : onlinePlayers) {
			if (!pl.getPlayerName().equals(this.getPlayerName()) && !this.visiblePlayers.contains(pl.getPlayerName()) && pl.getLocation().isInRange(50, this.getLocation()) && (!pl.isDead())) {
				this.visiblePlayers.add(pl.getPlayerName());
				this.networkManager.sendPacket(pl.getSpawnPacket());
			}
			if (!pl.getPlayerName().equals(this.getPlayerName()) && this.visiblePlayers.contains(pl.getPlayerName()) && !pl.getLocation().isInRange(50, this.getLocation())) {
				this.visiblePlayers.remove(pl.getPlayerName());
				toDespawn.add(pl.getEntityId());
			}
		}
		if (!toDespawn.isEmpty()) {
			this.networkManager.sendPacket(new PacketOutEntityDestroy(toDespawn.toArray(new Integer[0])));
		}
	}

	private int moveUpdates = 0;

	@Override
	public void broadcastLocation(Location newLocation) {

		double dx = (newLocation.getX() - this.getLocation().getX()) * 32;
		double dy = (newLocation.getY() - this.getLocation().getY()) * 32;
		double dz = (newLocation.getZ() - this.getLocation().getZ()) * 32;

		Packet packet;

		if (moveUpdates++ % 40 == 0 || dx > 127 || dx < -127 || dy > 127 || dy < -127 || dz > 127 || dz < -127) {
			// teleport
			packet = new PacketOutEntityTeleport(this.getEntityId(), (int) (this.getLocation().getX() * 32.0D), (int) (this.getLocation().getY() * 32.0D), (int) (this.getLocation().getZ() * 32.0D), (byte) this.getLocation().getYaw(), (byte) this.getLocation().getPitch(), false);
		} else {
			// movement
			packet = new PacketOutEntityRelativeMove(this.getEntityId(), (byte) dx, (byte) dy, (byte) dz, false);
		}

		Iterator<String> players = this.visiblePlayers.iterator();

		while (players.hasNext()) {
			EnderPlayer ep = Main.getInstance().getPlayer(players.next());

			if (ep == null) {
				players.remove();
				continue;
			}

			if (ep.getLocation().isInRange(50, this.getLocation())) {
				ep.networkManager.sendPacket(packet);
			}
		}
	}

	@Override
	public void broadcastRotation(float pitch, float yaw) {
		Iterator<String> players = this.visiblePlayers.iterator();

		Packet pack1 = new PacketOutEntityLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F), (byte) Utill.calcYaw(pitch * 256.0F / 360.0F), false);
		Packet pack2 = new PacketOutEntityHeadLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F));

		while (players.hasNext()) {
			EnderPlayer ep = Main.getInstance().getPlayer(players.next());

			if (ep == null) {
				players.remove();
				continue;
			}
			if (ep.getLocation().isInRange(50, this.getLocation())) {
				ep.networkManager.sendPacket(pack1);
				ep.networkManager.sendPacket(pack2);
			}
		}
	}

	public void playSound(String soundName, float volume, byte pitch) {
		networkManager.sendPacket(new PacketOutSoundEffect(soundName, getLocation().getBlockX(), getLocation().getBlockY(), getLocation().getBlockZ(), volume, pitch));
	}

	@Override
	public boolean sendMessage(Message message) {
		return this.sendRawMessage(message);
	}

	@Override
	public boolean sendRawMessage(Message message) {
		if (!this.isOnline)
			return false;
		try {
			this.networkManager.sendPacket(new PacketOutChatMessage(message, (byte) 0));
			return true;
		} catch (Exception ex) {
			try {
				this.networkManager.channelInactive(networkManager.ctx);
				EnderLogger.logger.throwing(null, null, ex);
			} catch (Exception ex1) {
				ex.addSuppressed(ex1);
				EnderLogger.logger.throwing(null, null, ex);
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return this.getPlayerName();
	}

	@Override
	public void teleport(Entity entity) {
		this.teleport(entity.getLocation());
	}

	@Override
	public void teleport(Location newLocation) {
		this.waitingForValidMoveAfterTeleport = 1;
		Location oldLocation = this.getLocation();
		oldLocation.setX(newLocation.getX());
		oldLocation.setY(newLocation.getY());
		oldLocation.setZ(newLocation.getZ());
		oldLocation.setPitch(newLocation.getPitch());
		oldLocation.setYaw(newLocation.getYaw());

		this.getNetworkManager().sendPacket(new PacketOutPlayerPositionLook(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch(), (byte) 0b00000));

		PacketOutEntityTeleport packet = new PacketOutEntityTeleport(this.getEntityId(), (int) (newLocation.getX() * 32.0D), (int) (newLocation.getY() * 32.0D), (int) (newLocation.getZ() * 32.0D), (byte) newLocation.getYaw(), (byte) newLocation.getPitch(), false);
		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if (!ep.equals(this)) {
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}

	public void onPlayerChatComplete(final PacketInTabComplete packet) {
		assert Thread.currentThread() != Main.getInstance().mainThread;
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				String message = packet.getHalfCommand();
				List<String> out;
				if (message.startsWith("/")) {
					final String fullCommand = message.substring(1);
					final String[] split = fullCommand.split(" ", -1);
					final String[] args;
					if (split.length != 1) {
						args = new String[split.length - 1];
						System.arraycopy(split, 1, args, 0, args.length);
					} else
						args = new String[0];
					String commandBeforeSplit = message.substring(0, message.length() - split[0].length());
					out = Main.getInstance().commands.executeTabList(null, split[0], EnderPlayer.this, args);
				} else {
					final String[] split = message.split(" ", -1);
					String lastPart = split[split.length - 1];
					String commandBeforeSplit = message.substring(0, message.length() - lastPart.length());
					out = Command.calculateMissingArgumentsPlayer(lastPart, EnderPlayer.this);
				}
				EnderPlayer.this.networkManager.sendPacket(new PacketOutTabComplete(out));
			}
		});
	}

	public boolean canSee(EnderPlayer player) {
		return true;
	}

	@Override
	public void damage(float damage) {
		if (damage <= 0) {
			throw new IllegalArgumentException("Damage cannot be smaller or equal to zero.");
		}
		if (this.godMode)
			return;
		super.damage(damage);
	}

	@Override
	protected void onHealthUpdate(float health, float oldHealth) {
		networkManager.sendPacket(new PacketOutUpdateHealth(health, food, foodSaturation));
		if (health > 0)
			return;
		Packet packet = new PacketOutEntityDestroy(new Integer[] { this.getEntityId() });
		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if (ep.visiblePlayers.contains(this.getPlayerName())) {
				ep.visiblePlayers.remove(this.getPlayerName());
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}

	@Override
	protected String getDamageSound() {
		return "game.player.hurt";
	}

	@Override
	protected String getDeadSound() {
		return "game.player.dead";
	}

	@Override
	protected float getBaseHealth() {
		return 20;
	}

	@Override
	protected float getBaseMaxHealth() {
		return 20;
	}

	public void setOnGround(boolean onGround) {
		if (this.isOnGround == false && onGround == true) {
			if (this.canFly)
				return; // Flying players don't get damage in vanilla
			// fall damage
			double change = this.yLocation - this.getLocation().getY() - 3;
			if (change > 0) {
				damage((float) change);
				if (change > 5) {
					// can't find correct sound name
				} else {
					Main.getInstance().mainWorld.broadcastSound("damage.fallsmall", getLocation().getBlockX(), getLocation().getBlockY(), getLocation().getBlockZ(), 1F, (byte) 63, getLocation(), null);
				}
			}
		} else if (this.isOnGround == true && onGround == false) {
			// save Y location
			this.yLocation = this.getLocation().getY();
		}
		this.isOnGround = onGround;
	}

	@Override
	public void onRightClick(EnderPlayer attacker) {
		// TODO
	}

	@Override
	public void onLeftClick(EnderPlayer attacker) {
		this.damage(1F);
	}

	@Override
	public boolean isValid() {
		return this.isOnline;
	}

	/**
	 * Class used to store the client side settings from the user, the reason I
	 * included setters and getters is that we can add a event or simulair to
	 * the code later on
	 *
	 * @author ferrybig
	 */
	public final class ClientSettings {

		private String locale = "en_US";
		private byte renderDistance = 3;
		private byte chatFlags = 0;
		private boolean chatColors = true;
		private int displayedSkinParts = 0;

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

		public boolean isChatColors() {
			return chatColors;
		}

		public void setChatColors(boolean chatColors) {
			this.chatColors = chatColors;
		}

		public int getDisplayedSkinParts() {
			return displayedSkinParts;
		}

		public void setDisplayedSkinParts(int displayedSkinParts) {
			this.displayedSkinParts = displayedSkinParts;
		}
	}
}
