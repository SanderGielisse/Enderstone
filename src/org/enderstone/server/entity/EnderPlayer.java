/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.enderstone.server.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.Utill;
import org.enderstone.server.api.ChatPosition;
import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Particle;
import org.enderstone.server.api.Vector;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.api.messages.ChatColor;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.inventory.InventoryHandler;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketInTabComplete;
import org.enderstone.server.packet.play.PacketOutBlockChange;
import org.enderstone.server.packet.play.PacketOutChangeGameState;
import org.enderstone.server.packet.play.PacketOutChatMessage;
import org.enderstone.server.packet.play.PacketOutChunkData;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityHeadLook;
import org.enderstone.server.packet.play.PacketOutEntityLook;
import org.enderstone.server.packet.play.PacketOutEntityMetadata;
import org.enderstone.server.packet.play.PacketOutEntityRelativeMove;
import org.enderstone.server.packet.play.PacketOutEntityTeleport;
import org.enderstone.server.packet.play.PacketOutPlayParticle;
import org.enderstone.server.packet.play.PacketOutPlayerAbilities;
import org.enderstone.server.packet.play.PacketOutPlayerListHeaderFooter;
import org.enderstone.server.packet.play.PacketOutPlayerListItem;
import org.enderstone.server.packet.play.PacketOutStatistics;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.Action;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.ActionAddPlayer;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.ActionRemovePlayer;
import org.enderstone.server.packet.play.PacketOutPlayerPositionLook;
import org.enderstone.server.packet.play.PacketOutRespawn;
import org.enderstone.server.packet.play.PacketOutSetExperience;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.packet.play.PacketOutSpawnPlayer;
import org.enderstone.server.packet.play.PacketOutTabComplete;
import org.enderstone.server.packet.play.PacketOutUpdateHealth;
import org.enderstone.server.permissions.Operator;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.EnderWorld.ChunkInformer;
import org.enderstone.server.regions.RegionSet;

public class EnderPlayer extends EnderEntity implements CommandSender, Player {

	private static final int MAX_CHUNKS_EVERY_UPDATE = 16;

	private final InventoryHandler inventoryHandler = new InventoryHandler(this);
	public final PlayerSettings clientSettings = new PlayerSettings();
	public final NetworkManager networkManager;
	public final EnumSet<PlayerDebugger> debugOutputs = EnumSet.noneOf(PlayerDebugger.class);

	public volatile boolean isOnline = true;
	public int keepAliveID = 0;
	private int entitySubId;

	public final String playerName;
	public final HashSet<String> visiblePlayers = new HashSet<>();
	public final HashSet<EnderEntity> canSeeEntity = new HashSet<>();
	public final UUID uuid;
	private final String textureValue;
	private final String textureSignature;

	/**
	 * Two fields used to determine the amount of fall damage
	 */
	public boolean isOnGround = true;
	public double yLocation;

	/**
	 * If this is above 0, then the server is waiting for a correction on the
	 * last teleport the server sended
	 */
	public int waitingForValidMoveAfterTeleport = 0;

	/**
	 * 
	 * The loadedChunks regionSet is a kinda "HashMap" for chunks, but a bit
	 * faster.
	 * 
	 * The chunkInformer is a small interface which converts our "chunk data"
	 * into sendable packets. This interface is currently also being used for
	 * caching the sending chunks.
	 */
	private final RegionSet loadedChunks = new RegionSet();
	public ChunkInformer chunkInformer = new ChunkInformer() {

		private List<EnderChunk> cache = new ArrayList<>();

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

	public EnderPlayer(EnderWorld world, String userName, NetworkManager networkManager, UUID uuid, PlayerTextureStore textures) {
		super(world.getSpawn().clone());
		this.entitySubId = super.getEntityId();
		this.networkManager = networkManager;
		this.playerName = userName;
		this.uuid = uuid;
		this.textureValue = textures.getSkin().value;
		this.textureSignature = textures.getSkin().signature;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + entitySubId;
		result = prime * result + ((textureSignature == null) ? 0 : textureSignature.hashCode());
		result = prime * result + ((textureValue == null) ? 0 : textureValue.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnderPlayer other = (EnderPlayer) obj;
		if (entitySubId != other.entitySubId)
			return false;
		if (textureSignature == null) {
			if (other.textureSignature != null)
				return false;
		} else if (!textureSignature.equals(other.textureSignature))
			return false;
		if (textureValue == null) {
			if (other.textureValue != null)
				return false;
		} else if (!textureValue.equals(other.textureValue))
			return false;
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

	public void switchWorld(EnderWorld toWorld) {
		this.getNetworkManager().sendPacket(new PacketOutRespawn(0, (byte) 0, (byte) GameMode.SURVIVAL.getId(), "default"));
		EnderWorld currentWorld = this.getWorld();
		boolean succes = currentWorld.players.remove(this);
		assert succes;
		toWorld.players.add(this);
		this.getLocation().cloneFrom(toWorld.getSpawn());
		this.loadedChunks.clear();
		toWorld.doChunkUpdatesForPlayer(this, this.chunkInformer, 3);
		networkManager.player.getInventoryHandler().updateInventory();
		this.getNetworkManager().sendPacket(new PacketOutPlayerPositionLook(toWorld.getSpawn().getX(), toWorld.getSpawn().getY(), toWorld.getSpawn().getZ(), 0F, 0F, (byte) 1));
		this.updateClientSettings();
		EnderLogger.info("Switched player " + this.getPlayerName() + " from world " + currentWorld.worldName + " to " + toWorld.worldName + ".");
	}

	public ProfileProperty[] getProfileProperties() {
		ProfileProperty[] list = new ProfileProperty[1];
		list[0] = getTextureProperty();
		return list;
	}

	@Override
	public void onSpawn() {
		EnderLogger.info(this.getPlayerName() + " logged in from " + networkManager.ctx.channel().remoteAddress().toString() + " with uuid " + uuid);
		this.inventoryHandler.tryPickup(new ItemStack(BlockId.DIAMOND_PICKAXE.getId(), (byte) 1, (short) 0));
		this.inventoryHandler.tryPickup(new ItemStack(BlockId.DIAMOND_SPADE.getId(), (byte) 1, (short) 0));
		this.inventoryHandler.tryPickup(new ItemStack(BlockId.DIRT.getId(), (byte) 64, (short) 0));
		this.inventoryHandler.tryPickup(new ItemStack(BlockId.WORKBENCH.getId(), (byte) 1, (short) 0));
		this.inventoryHandler.tryPickup(new ItemStack(BlockId.CHEST.getId(), (byte) 1, (short) 0));
		this.updateDataWatcher();

		this.getNetworkManager().sendPacket(new PacketOutPlayerListHeaderFooter(this.clientSettings.tabListHeader, this.clientSettings.tabListFooter));
		
		PacketOutPlayerListItem packet = new PacketOutPlayerListItem(new Action[] { new ActionAddPlayer(this.uuid, this.getPlayerName(), getProfileProperties(), GameMode.SURVIVAL.getId(), 1, false, "") });
		for (EnderPlayer player : Main.getInstance().onlinePlayers) {
			player.getNetworkManager().sendPacket(packet);
			this.getNetworkManager().sendPacket(new PacketOutPlayerListItem(new Action[] { new ActionAddPlayer(player.uuid, player.getPlayerName(), player.getProfileProperties(), GameMode.SURVIVAL.getId(), 1, false, "") }));
		}
		Main.getInstance().broadcastMessage(new SimpleMessage(ChatColor.YELLOW + this.getPlayerName() + " joined the game!"));
	}

	@Override
	public void updateDataWatcher() {
		int meaning = 0;

		if (this.clientSettings.isOnFire)
			meaning = (byte) (meaning | 0x01);
		if (this.clientSettings.isSneaking)
			meaning = (byte) (meaning | 0x02);
		if (this.clientSettings.isSprinting)
			meaning = (byte) (meaning | 0x08);
		if (this.clientSettings.isEating)
			meaning = (byte) (meaning | 0x10);
		if (this.clientSettings.isInvisible)
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
		if (Main.getInstance().getWorld(this).players.contains(this)) {
			Main.getInstance().getWorld(this).players.remove(this);
		}

		for (EnderPlayer p : Main.getInstance().onlinePlayers) {
			p.getNetworkManager().sendPacket(new PacketOutPlayerListItem(new Action[] { new ActionRemovePlayer(this.uuid) }));
		}
		if (Main.getInstance().onlinePlayers.contains(this)) {
			Main.getInstance().onlinePlayers.remove(this);
			for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
				for (String name : ep.visiblePlayers) {
					if (name.equals(this.getPlayerName()) && !this.getPlayerName().equals(ep.getPlayerName())) {
						ep.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[] { this.getEntityId() }));
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
	public void updatePlayers(Set<EnderPlayer> onlinePlayers) {
		Set<Integer> toDespawn = new HashSet<>();
		for (EnderPlayer pl : onlinePlayers) {
			if (!pl.getPlayerName().equals(this.getPlayerName()) && !this.visiblePlayers.contains(pl.getPlayerName()) && pl.getLocation().isInRange(50, this.getLocation(), true) && (!pl.isDead())) {
				this.visiblePlayers.add(pl.getPlayerName());
				this.networkManager.sendPacket(pl.getSpawnPacket());
			}
			if (!pl.getPlayerName().equals(this.getPlayerName()) && this.visiblePlayers.contains(pl.getPlayerName()) && !pl.getLocation().isInRange(50, this.getLocation(), true)) {
				this.visiblePlayers.remove(pl.getPlayerName());
				toDespawn.add(pl.getEntityId());
			}
		}
		if (!toDespawn.isEmpty()) {
			this.networkManager.sendPacket(new PacketOutEntityDestroy(toDespawn.toArray(new Integer[0])));
		}
	}

	private int latestCheck = 0;
	private final List<EnderEntity> toRemove = new ArrayList<>();

	public void checkCollision() {
		if (latestCheck++ % 3 == 0) {
			// check if item entities nearby
			for (EnderEntity e : Main.getInstance().getWorld(this).entities) {
				if (e.getLocation().isInRange(2, this.getLocation(), true)) {
					boolean remove = e.onCollision(this);
					if (remove) {
						toRemove.add(e);
					}
				}
			}
			for (EnderEntity e : toRemove) {
				Main.getInstance().getWorld(this).removeEntity(e);
				Main.getInstance().getWorld(this).broadcastSound("random.pop", 1F, (byte) 63, this.getLocation(), null);
			}
			toRemove.clear();
		}
	}

	private int moveUpdates = 0;

	@Override
	public void broadcastLocation(Location newLocation) {
		if (this.isDead()) {
			return;
		}
		checkCollision();

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

			if (ep.getLocation().isInRange(50, this.getLocation(), true)) {
				ep.networkManager.sendPacket(packet);
			}
		}
	}

	@Override
	public void broadcastRotation(float pitch, float yaw) {
		if (isDead()) {
			return;
		}

		Iterator<String> players = this.visiblePlayers.iterator();

		Packet pack1 = new PacketOutEntityLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F), (byte) Utill.calcYaw(pitch * 256.0F / 360.0F), false);
		Packet pack2 = new PacketOutEntityHeadLook(this.getEntityId(), (byte) Utill.calcYaw(yaw * 256.0F / 360.0F));

		while (players.hasNext()) {
			EnderPlayer ep = Main.getInstance().getPlayer(players.next());

			if (ep == null) {
				players.remove();
				continue;
			}
			if (ep.getLocation().isInRange(50, this.getLocation(), true)) {
				ep.networkManager.sendPacket(pack1);
				ep.networkManager.sendPacket(pack2);
			}
		}
	}

	public void debug(String message, PlayerDebugger level) {
		if (level == null)
			level = PlayerDebugger.OTHER;
		if (debugOutputs.contains(level))
			sendRawMessage(new SimpleMessage(message));
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
			this.networkManager.sendPacket(new PacketOutChatMessage(message, (byte) 1));
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

	public void updateClientSettings() { // this will also be called when a player switches world
		this.networkManager.sendPacket(new PacketOutChangeGameState((byte) 3, this.clientSettings.gameMode.getId()));
		this.updateAbilities(); //do this after sending the gamemode
		// TODO send player equipment
	}

	@Override
	public String getName() {
		return this.getPlayerName();
	}

	@Override
	public void teleport(EnderEntity entity) {
		this.teleport(entity.getLocation());
	}

	@Override
	public void teleport(Location newLocation) {
		this.waitingForValidMoveAfterTeleport = 1;
		this.getLocation().cloneFrom(newLocation);

		this.getNetworkManager().sendPacket(new PacketOutPlayerPositionLook(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch(), (byte) 0b00000));

		PacketOutEntityTeleport packet = new PacketOutEntityTeleport(this.getEntityId(), (int) (newLocation.getX() * 32.0D), (int) (newLocation.getY() * 32.0D), (int) (newLocation.getZ() * 32.0D), (byte) newLocation.getYaw(), (byte) newLocation.getPitch(), false);
		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if (!ep.equals(this)) {
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}
	
	private int latestHeal = 0;
	private int latestFood = 0;
	
	@Override
	public void serverTick() {
		if (!this.isDead() && latestFood++ % (30 * 20) == 0) { //TODO do this how it goes in default Minecraft
			if ((this.getFood() - 1) >= 0) {
				this.setFood((short) (this.getFood() - 1));
			} else {
				this.damage(1F);
			}
		}
		if (!this.isDead() && latestHeal++ % (15 * 20) == 0) { //TODO do this how it goes in default Minecraft
			if ((this.getHealth() + 0.5F) <= this.getMaxHealth() && this.getFood() > 0) {
				this.setHealth(this.getHealth() + 0.5F);
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
					out = Main.getInstance().commands.executeTabList(null, split[0], EnderPlayer.this, args);
				} else {
					final String[] split = message.split(" ", -1);
					String lastPart = split[split.length - 1];
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
		if (this.clientSettings.godMode)
			return;
		super.damage(damage);
	}

	@Override
	protected void onHealthUpdate(float health, float oldHealth) {
		networkManager.sendPacket(new PacketOutUpdateHealth(health, clientSettings.food, clientSettings.foodSaturation));
		if (health > 0) {
			return;
		}
		this.clientSettings.food = 20;
		Packet packet = new PacketOutEntityDestroy(new Integer[] { this.getEntityId() });
		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if (ep.visiblePlayers.contains(this.getPlayerName())) {
				ep.visiblePlayers.remove(this.getPlayerName());
				ep.getNetworkManager().sendPacket(packet);
			}
		}
		for (ItemStack inv : this.getInventoryHandler().getPlayerInventory().getRawItems()) {
			if (inv != null) {
				EnderWorld world = Main.getInstance().getWorld(this);
				world.dropItem(getLocation(), inv, 1);
			}
		}
		Collections.fill(this.getInventoryHandler().getPlayerInventory().getRawItems(), null);
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

	@Override
	public boolean hasPermission(String permission) {
		for (Operator operator : Main.getInstance().operators) {
			if (operator.getUUID().equals(this.uuid)) {
				return true;
			}
		}
		return false;
	}

	public void setOnGround(boolean onGround) {
		if (this.isOnGround == false && onGround == true) {
			if (this.clientSettings.isFlying)
				return; // Flying players don't get damage in vanilla
			// fall damage
			double change = this.yLocation - this.getLocation().getY() - 3;
			if (change > 0) {
				damage((float) change);
				if (change > 5) {
					// can't find correct sound name
				} else {
					Main.getInstance().getWorld(this).broadcastSound("damage.fallsmall", 1F, (byte) 63, getLocation(), null);
				}
			}
		} else if (this.isOnGround == true && onGround == false) {
			// save Y location
			this.yLocation = this.getLocation().getY();
		}
		this.isOnGround = onGround;
	}

	public void sendBlockUpdate(Location loc, short blockId, byte dataValue) {
		this.getNetworkManager().sendPacket(new PacketOutBlockChange(loc, blockId, dataValue));
	}

	@Override
	public void onRightClick(EnderPlayer attacker) {
		// TODO
	}

	@Override
	public void onLeftClick(EnderPlayer attacker) {
		this.damage(1F, Vector.substract(attacker.getLocation(), this.getLocation()).normalize(this.getLocation().distance(attacker.getLocation())));
	}

	public int getFood() {
		return this.clientSettings.food;
	}

	public void setFood(short foodLevel) {
		if (foodLevel < 0 || foodLevel > 20) {
			throw new IllegalArgumentException(foodLevel + " is not a valid food level value");
		}
		this.clientSettings.food = foodLevel;
		this.getNetworkManager().sendPacket(new PacketOutUpdateHealth(getHealth(), this.clientSettings.food, 0));
	}

	public EnderWorld getWorld() {
		return Main.getInstance().getWorld(this);
	}

	@Override
	public boolean isValid() {
		return this.isOnline;
	}

	@Override
	public void kick(String reason) {
		this.getNetworkManager().disconnect(new SimpleMessage(reason), false);
	}

	/**
	 * @return the inventoryHandler
	 */
	public InventoryHandler getInventoryHandler() {
		return inventoryHandler;
	}

	public void updateAbilities() {
		int i = 0;
		if (this.clientSettings.isCreative)
			i = (byte) (i | 0x1);
		if (this.clientSettings.isFlying)
			i = (byte) (i | 0x2);
		if (this.clientSettings.allowFlight)
			i = (byte) (i | 0x4);
		if (this.clientSettings.godMode)
			i = (byte) (i | 0x8);

		float fly = this.clientSettings.flySpeed;
		float walk = this.clientSettings.walkSpeed;
		this.networkManager.sendPacket(new PacketOutPlayerAbilities((byte) i, fly, walk));
	}

	public enum PlayerDebugger {
		INVENTORY, PACKET, OTHER,
	}

	public RegionSet getLoadedChunks() {
		return this.loadedChunks;
	}

	@Override
	public void sendMessage(Message message, ChatPosition position) {
		this.getNetworkManager().sendPacket(new PacketOutChatMessage(message, (byte) position.getId()));
	}

	@Override
	public int getFoodLevel() {
		return clientSettings.food;
	}

	@Override
	public void setFoodLevel(int foodLevel) {
		this.setFood((short) foodLevel);
	}

	@Override
	public boolean getAllowFlight() {
		return this.clientSettings.allowFlight;
	}

	@Override
	public void setAllowFlight(boolean allowFlight) {
		this.clientSettings.allowFlight = allowFlight;
		this.updateAbilities();
	}

	@Override
	public boolean isFlying() {
		return this.clientSettings.isFlying;
	}

	@Override
	public void setFlying(boolean flying) {
		this.clientSettings.isFlying = flying;
		this.updateAbilities();
	}

	@Override
	public float getFlySpeed() {
		return this.clientSettings.flySpeed;
	}

	@Override
	public void setFlySpeed(float speed) {
		this.clientSettings.flySpeed = speed;
		this.updateAbilities();
	}

	@Override
	public float getWalkSpeed() {
		return this.clientSettings.walkSpeed;
	}

	@Override
	public void setWalkSpeed(float speed) {
		this.clientSettings.walkSpeed = speed;
		this.updateAbilities();
	}

	@Override
	public int getExperience() {
		return this.clientSettings.experience;
	}

	@Override
	public int getExperienceLevel() {
		return this.clientSettings.experience; // TODO convert this to a level
	}

	@Override
	public void setExperience(int experience) {
		this.clientSettings.experience = experience;
		this.networkManager.sendPacket(new PacketOutSetExperience(0F, this.clientSettings.experience, this.clientSettings.experience)); // TODO calculate this correctly
	}

	@Override
	public void setExperienceLevel(int experienceLevel) {
		this.clientSettings.experience = experienceLevel; // TODO convert this to a level
		this.networkManager.sendPacket(new PacketOutSetExperience(0F, this.clientSettings.experience, this.clientSettings.experience)); // TODO calculate this correctly
	}

	@Override
	public boolean canSprint() {
		return this.clientSettings.food <= 3;
	}

	@Override
	public boolean isOnGround() {
		return this.isOnGround;
	}

	@Override
	public boolean isSneaking() {
		return clientSettings.isSneaking;
	}

	@Override
	public void setSneaking(boolean sneaking) {
		clientSettings.isSneaking = sneaking;
		this.updateDataWatcher();
		this.getWorld().broadcastPacket(new PacketOutEntityMetadata(this.getEntityId(), this.getDataWatcher()), this.getLocation());
	}

	@Override
	public void playSound(String soundName, int volume, int pitch) {
		this.networkManager.sendPacket(new PacketOutSoundEffect(soundName, getLocation().getBlockX(), getLocation().getBlockY(), getLocation().getBlockZ(), pitch, (byte) volume));
	}

	@Override
	public void playParticle(Particle particle, Location location, float xOffset, float yOffset, float zOffset, float data, int amount) {
		this.networkManager.sendPacket(new PacketOutPlayParticle(particle.getId(), location, xOffset, yOffset, zOffset, data, amount));
	}

	@Override
	public void sendBlockUpdate(Location loc, BlockId blockId, byte dataValue) {
		this.networkManager.sendPacket(new PacketOutBlockChange(loc, blockId, dataValue));
	}

	@Override
	public String getDisplayName() {
		if (clientSettings.displayName == null) {
			clientSettings.displayName = getPlayerName();
		}
		return clientSettings.displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		clientSettings.displayName = displayName;
	}

	@Override
	public void closeInventory() {
		this.inventoryHandler.openInventory(null);
	}

	@Override
	public GameMode getGameMode() {
		return this.clientSettings.gameMode;
	}

	public void setGameMode(GameMode mode) {
		this.clientSettings.gameMode = mode;
		byte reason = 3; // gamemode change
		int value = mode.getId(); // gamemode id
		this.networkManager.sendPacket(new PacketOutChangeGameState(reason, value));
	}

	@Override
	public InventoryHandler getInventory() {
		return this.getInventoryHandler();
	}

	@Override
	public ItemStack getItemInHand() {
		return this.getInventoryHandler().getItemInHand();
	}

	@Override
	public void awardAchievment(String name) {
		this.getNetworkManager().sendPacket(new PacketOutStatistics(name, 1));
	}

	@Override
	public void updateStatistic(String name, int value) {
		this.getNetworkManager().sendPacket(new PacketOutStatistics(name, value));
	}

	@Override
	public void forceChat(String rawMessage) {
		this.onPlayerChat(rawMessage);
	}

	@Override
	public boolean isInAir() {
		return !isOnGround;
	}

	@Override
	public void setTabListHeader(Message header) {
		this.clientSettings.tabListHeader = header;
		this.getNetworkManager().sendPacket(new PacketOutPlayerListHeaderFooter(header, this.clientSettings.tabListFooter));
	}

	@Override
	public void setTabListFooter(Message footer) {
		this.clientSettings.tabListFooter = footer;
		this.getNetworkManager().sendPacket(new PacketOutPlayerListHeaderFooter(this.clientSettings.tabListHeader, footer));
	}

	@Override
	public void setTabListHeaderAndFooter(Message header, Message footer) {
		this.clientSettings.tabListHeader = header;
		this.clientSettings.tabListFooter = footer;
		this.getNetworkManager().sendPacket(new PacketOutPlayerListHeaderFooter(header, footer));
	}

	@Override
	public Message getTabListHeader() {
		return this.clientSettings.tabListHeader;
	}

	@Override
	public Message getTabListFooter() {
		return this.clientSettings.tabListFooter;
	}
}
