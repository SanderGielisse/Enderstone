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
import org.enderstone.server.api.ChatPosition;
import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Particle;
import org.enderstone.server.api.Vector;
import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.api.event.player.PlayerChatEvent;
import org.enderstone.server.api.event.player.PlayerCommandEvent;
import org.enderstone.server.api.event.player.PlayerExpChangeEvent;
import org.enderstone.server.api.event.player.PlayerGamemodeChangeEvent;
import org.enderstone.server.api.event.player.PlayerKickEvent;
import org.enderstone.server.api.event.player.PlayerTeleportEvent;
import org.enderstone.server.api.event.player.PlayerToggleSneakEvent;
import org.enderstone.server.api.event.player.PlayerToggleSprintEvent;
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.api.messages.ChatColor;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.inventory.Inventory;
import org.enderstone.server.inventory.InventoryHandler;
import org.enderstone.server.inventory.InventoryListener;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.inventory.PlayerInventory;
import org.enderstone.server.inventory.armour.Armor;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketInTabComplete;
import org.enderstone.server.packet.play.PacketOutBlockChange;
import org.enderstone.server.packet.play.PacketOutChangeGameState;
import org.enderstone.server.packet.play.PacketOutChatMessage;
import org.enderstone.server.packet.play.PacketOutChunkData;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityEquipment;
import org.enderstone.server.packet.play.PacketOutEntityHeadLook;
import org.enderstone.server.packet.play.PacketOutEntityLook;
import org.enderstone.server.packet.play.PacketOutEntityMetadata;
import org.enderstone.server.packet.play.PacketOutEntityRelativeMove;
import org.enderstone.server.packet.play.PacketOutEntityStatus;
import org.enderstone.server.packet.play.PacketOutEntityStatus.Status;
import org.enderstone.server.packet.play.PacketOutEntityTeleport;
import org.enderstone.server.packet.play.PacketOutPlayParticle;
import org.enderstone.server.packet.play.PacketOutPlayerAbilities;
import org.enderstone.server.packet.play.PacketOutPlayerListHeaderFooter;
import org.enderstone.server.packet.play.PacketOutPlayerListItem;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.Action;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.ActionAddPlayer;
import org.enderstone.server.packet.play.PacketOutPlayerListItem.ActionRemovePlayer;
import org.enderstone.server.packet.play.PacketOutPlayerPositionLook;
import org.enderstone.server.packet.play.PacketOutRespawn;
import org.enderstone.server.packet.play.PacketOutSetExperience;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.packet.play.PacketOutSpawnPlayer;
import org.enderstone.server.packet.play.PacketOutStatistics;
import org.enderstone.server.packet.play.PacketOutTabComplete;
import org.enderstone.server.packet.play.PacketOutTitle;
import org.enderstone.server.packet.play.PacketOutTitle.ActionDisplayTitle;
import org.enderstone.server.packet.play.PacketOutTitle.ActionSubtitle;
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
	{
		inventoryHandler.getPlayerInventory().addListener(new InventoryListener() {

			@Override
			public void onSlotChange(Inventory inv, int slot, ItemStack oldStack, ItemStack newStack) {
				if (slot > 35 && slot < 45) {
					broadcastEquipment(EquipmentUpdateType.ITEM_IN_HAND_CHANGE);
				} else if (slot == 5) {
					broadcastEquipment(EquipmentUpdateType.HELMET_CHANGE);
				} else if (slot == 6) {
					broadcastEquipment(EquipmentUpdateType.CHESTPLATE_CHANGE);
				} else if (slot == 7) {
					broadcastEquipment(EquipmentUpdateType.LEGGINGS_CHANGE);
				} else if (slot == 8) {
					broadcastEquipment(EquipmentUpdateType.BOOTS_CHANGE);
				}
			}

			@Override
			public void onPropertyChange(Inventory inv, short property, short oldValue, short newValue) {}

			@Override
			public void closeInventory(Inventory inv) {}
		});
	}
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
	 * If this is above 0, then the server is waiting for a correction on the last teleport the server sended
	 */
	public int waitingForValidMoveAfterTeleport = 0;

	/**
	 * 
	 * The loadedChunks regionSet is a kinda "HashMap" for chunks, but a bit faster.
	 * 
	 * The chunkInformer is a small interface which converts our "chunk data" into sendable packets. This interface is currently also being used for caching the sending chunks.
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
		this.setLocation(this.getLocation().cloneFrom(toWorld.getSpawn()));
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

		this.inventoryHandler.tryPickup(new ItemStack(BlockId.COOKED_CHICKEN, (byte) 10));
		this.inventoryHandler.tryPickup(new ItemStack(BlockId.RAW_CHICKEN, (byte) 10));
		this.inventoryHandler.tryPickup(new ItemStack(BlockId.RAW_RABBIT, (byte) 10));

		this.inventoryHandler.getPlayerInventory().setRawItem(5, new ItemStack(BlockId.DIAMOND_HELMET, (byte) 1));

		this.updateDataWatcher();

		this.getNetworkManager().sendPacket(new PacketOutPlayerListHeaderFooter(this.clientSettings.tabListHeader, this.clientSettings.tabListFooter));

		PacketOutPlayerListItem packet = new PacketOutPlayerListItem(new Action[] { new ActionAddPlayer(this.uuid, this.getPlayerName(), getProfileProperties(), this.clientSettings.gameMode.getId(), 1, false, "") });
		for (EnderPlayer player : Main.getInstance().onlinePlayers) {
			player.getNetworkManager().sendPacket(packet);
			this.getNetworkManager().sendPacket(new PacketOutPlayerListItem(new Action[] { new ActionAddPlayer(player.uuid, player.getPlayerName(), player.getProfileProperties(), player.clientSettings.gameMode.getId(), 1, false, "") }));
		}
		Main.getInstance().broadcastMessage(new AdvancedMessage().getBase().setText(this.getPlayerName() + " joined the game!").setColor(ChatColor.YELLOW).build());
	}

	@Override
	public void updateDataWatcher() {
		int meaning = 0;

		if (this.getFireTicks() > 0)
			meaning = (byte) (meaning | 0x01);
		if (this.clientSettings.isSneaking)
			meaning = (byte) (meaning | 0x02);
		if (this.clientSettings.isSprinting)
			meaning = (byte) (meaning | 0x08);
		if (this.clientSettings.isEatingTicks > 0)
			meaning = (byte) (meaning | 0x10);
		if (this.clientSettings.isInvisible)
			meaning = (byte) (meaning | 0x20);

		this.getDataWatcher().watch(0, (byte) meaning);
		this.getDataWatcher().watch(1, (short) 0);
		this.getDataWatcher().watch(6, getHealth());
		this.getDataWatcher().watch(8, (byte) 0);
	}

	@Override
	public Packet[] getSpawnPackets() {
		List<Packet> toSend = new ArrayList<>();
		PlayerInventory handler = this.getInventoryHandler().getPlayerInventory();
		toSend.add(new PacketOutSpawnPlayer(this.getEntityId(), this.uuid, (int) (this.getLocation().getX() * 32.0D), (int) (this.getLocation().getY() * 32.0D), (int) (this.getLocation().getZ() * 32.0D), (byte) 0, (byte) 0, (short) 0, this.getDataWatcher()));
		if (this.getInventoryHandler().getItemInHand() != null) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 0, this.getInventoryHandler().getItemInHand())); // helmet
		}
		if (handler.getArmor().get(0) != null) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 4, handler.getArmor().get(0))); // helmet
		}
		if (handler.getArmor().get(1) != null) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 3, handler.getArmor().get(0))); // chestplate
		}
		if (handler.getArmor().get(2) != null) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 2, handler.getArmor().get(0))); // leggins
		}
		if (handler.getArmor().get(3) != null) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 1, handler.getArmor().get(0))); // boots
		}
		return toSend.toArray(new Packet[toSend.size()]);
	}

	public void broadcastEquipment(EquipmentUpdateType type) {
		List<Packet> toSend = new ArrayList<>();
		List<ItemStack> handler = this.getInventoryHandler().getPlayerInventory().getArmor();

		if ((type == EquipmentUpdateType.ITEM_IN_HAND_CHANGE || type == EquipmentUpdateType.ALL)) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 0, this.getInventoryHandler().getItemInHand())); // item in hand
		}
		if ((type == EquipmentUpdateType.HELMET_CHANGE || type == EquipmentUpdateType.ALL)) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 4, handler.get(0))); // helmet
		}
		if ((type == EquipmentUpdateType.CHESTPLATE_CHANGE || type == EquipmentUpdateType.ALL)) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 3, handler.get(0))); // chestplate
		}
		if ((type == EquipmentUpdateType.LEGGINGS_CHANGE || type == EquipmentUpdateType.ALL)) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 2, handler.get(0))); // leggins
		}
		if ((type == EquipmentUpdateType.BOOTS_CHANGE || type == EquipmentUpdateType.ALL)) {
			toSend.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 1, handler.get(0))); // boots
		}
		Iterator<String> visible = this.visiblePlayers.iterator();
		while (visible.hasNext()) {
			String name = visible.next();
			EnderPlayer ep = Main.getInstance().getPlayer(name);
			if (ep == null) {
				visible.remove();
				continue;
			}
			for (Packet pack : toSend) {
				ep.getNetworkManager().sendPacket(pack);
			}
		}
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
					if (!Main.getInstance().callEvent(new PlayerCommandEvent(EnderPlayer.this, split[0], args))) {
						Main.getInstance().commands.executeCommand(null, split[0], EnderPlayer.this, args);
					}
				}
			});
		} else {
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					PlayerChatEvent e = new PlayerChatEvent(EnderPlayer.this, message);
					Main.getInstance().callEvent(e);
					// example format: <%name> %message
					if (!e.isCancelled()) {
						Main.getInstance().broadcastMessage(new SimpleMessage(e.getFormat().replace("%name", getPlayerName()).replace("%message", e.getMessage())));
					}
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
		Main.getInstance().broadcastMessage(new AdvancedMessage().getBase().setText(playerName + " left the game!").setColor(ChatColor.YELLOW).build());
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
				this.networkManager.sendPacket(pl.getSpawnPackets());
			}
			if (!pl.getPlayerName().equals(this.getPlayerName()) && this.visiblePlayers.contains(pl.getPlayerName()) && !pl.getLocation().isInRange(50, this.getLocation(), true)) {
				this.visiblePlayers.remove(pl.getPlayerName());
				toDespawn.add(pl.getEntityId());
			}
		}

		Iterator<String> it = this.visiblePlayers.iterator();
		while (it.hasNext()) {
			String name = it.next();
			if (Main.getInstance().getPlayer(name) == null) {
				it.remove();
			}
		}
		if (!toDespawn.isEmpty()) {
			this.networkManager.sendPacket(new PacketOutEntityDestroy(toDespawn.toArray(new Integer[0])));
		}
	}

	private int latestCheck = 0;

	public void checkCollision() {
		if (latestCheck++ % 3 == 0) {
			// check if item entities nearby
			Iterator<EnderEntity> it = Main.getInstance().getWorld(this).entities.iterator();
			while (it.hasNext()) {
				EnderEntity e = it.next();
				if (e.getLocation().isInRange(2, this.getLocation(), true)) {
					boolean remove = e.onCollision(this);
					if (remove) {
						it.remove();
						Main.getInstance().getWorld(this).removeEntity(e, true);
						Main.getInstance().getWorld(this).broadcastSound("random.pop", 1F, (byte) 63, this.getLocation(), null);
					}
				}
			}
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

		Packet pack1 = new PacketOutEntityLook(this.getEntityId(), (byte) calcYaw(yaw * 256.0F / 360.0F), (byte) calcYaw(pitch * 256.0F / 360.0F), false);
		Packet pack2 = new PacketOutEntityHeadLook(this.getEntityId(), (byte) calcYaw(yaw * 256.0F / 360.0F));

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
		this.updateAbilities(); // do this after sending the gamemode
		this.inventoryHandler.updateInventory();
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
		if (Main.getInstance().callEvent(new PlayerTeleportEvent(this, this.getLocation(), newLocation))) {
			return;
		}
		this.teleportInternally(newLocation);
	}

	public void teleportInternally(Location newLocation) {
		this.waitingForValidMoveAfterTeleport = 1;
		this.setLocation(this.getLocation().cloneFrom(newLocation));

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
		super.serverTick();
		if (Main.getInstance().doPhysics == false) {
			return;
		}
		if (getFoodLevel() == 20) {
			this.clientSettings.isEatingTicks = 0;
			this.getNetworkManager().sendPacket(new PacketOutUpdateHealth(this.getHealth(), this.getFoodLevel(), this.clientSettings.foodSaturation));
		} else {
			if (this.clientSettings.isEatingTicks > 0) {
				this.clientSettings.isEatingTicks++;
				if (this.clientSettings.isEatingTicks % 30 == 0) {
					this.clientSettings.isEatingTicks = 0;
					if (this.getInventoryHandler().getItemInHand() != null) {
						FoodType type = FoodType.fromBlockId(this.getInventoryHandler().getItemInHand().getBlockId());
						this.getInventoryHandler().decreaseItemInHand(1);

						if (this.clientSettings.foodSaturation + type.getSaturation() > 5) {
							this.clientSettings.foodSaturation = 5;
						} else {
							this.clientSettings.foodSaturation = this.clientSettings.foodSaturation + type.getSaturation();
						}

						if (this.getFoodLevel() <= (20 - type.getFood())) {
							this.setFoodLevel(this.getFoodLevel() + type.getFood());
						} else {
							this.setFoodLevel(20);
						}
					}
				}
			}
		}

		boolean didFoodUpdate = false;
		latestFood++;
		if (!this.isDead() && ((latestFood % (10 * 20) == 0 && this.isSprinting()) || (latestFood % (50 * 20) == 0 && !this.isSprinting()))) {
			didFoodUpdate = true;
			if (this.clientSettings.foodSaturation > 0) {
				this.clientSettings.foodSaturation--;
			} else {
				if ((this.getFoodLevel() - 1) >= 0) {
					this.setFood((short) (this.getFoodLevel() - 1));
				} else {
					if (this.damage(1F)) {
						Main.getInstance().broadcastMessage(new SimpleMessage(this.getPlayerName() + " starved to death."));
					}
				}
			}
		}
		if (!this.isDead() && latestHeal++ % (15 * 20) == 0 && !didFoodUpdate) {
			if ((this.getHealth() + 0.5F) <= this.getMaxHealth() && this.getFoodLevel() > 0) {
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
	public boolean damage(float damage) {
		if (damage <= 0) {
			throw new IllegalArgumentException("Damage cannot be smaller or equal to zero.");
		}
		if (this.clientSettings.godMode)
			return false;

		for (ItemStack stack : this.getInventoryHandler().getPlayerInventory().getArmor()) {
			if (stack != null) {
				Armor armor = Armor.fromId(stack.getId());
				if (armor != null) {
					damage = damage * armor.getDamageMultiplier();
				}
			}
		}
		return super.damage(damage);
	}

	@Override
	protected void onHealthUpdate(float health, float oldHealth) {
		networkManager.sendPacket(new PacketOutUpdateHealth(health, clientSettings.food, clientSettings.foodSaturation));
		if (health > 0) {
			return;
		}
		broadcastEmptyArmour();
		// player is dead
		this.clientSettings.food = 20;
		Packet packet = new PacketOutEntityStatus(this.getEntityId(), Status.LIVING_ENTITY_DEAD);
		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if (ep.visiblePlayers.contains(this.getPlayerName())) {
				ep.visiblePlayers.remove(this.getPlayerName());
				ep.getNetworkManager().sendPacket(packet);
			}
		}
		this.canSeeEntity.clear();
		for (ItemStack inv : this.getInventoryHandler().getPlayerInventory().getRawItems()) {
			if (inv != null) {
				EnderWorld world = Main.getInstance().getWorld(this);
				world.dropItem(getLocation(), inv, 1);
			}
		}
		Collections.fill(this.getInventoryHandler().getPlayerInventory().getRawItems(), null);
	}

	private void broadcastEmptyArmour() {
		// broadcast the players armour empty, otherwise the client will regenrate "fake" on-ground items.
		List<Packet> tmp = new ArrayList<>();
		tmp.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 0, null));
		tmp.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 1, null));
		tmp.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 2, null));
		tmp.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 3, null));
		tmp.add(new PacketOutEntityEquipment(this.getEntityId(), (short) 4, null));

		Iterator<String> it = this.visiblePlayers.iterator();

		while (it.hasNext()) {
			String name = it.next();
			EnderPlayer ep = Main.getInstance().getPlayer(name);
			if (ep == null) {
				it.remove();
				continue;
			}
			ep.getNetworkManager().sendPacket(tmp.toArray(new Packet[tmp.size()]));
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
		if (Main.getInstance().doPhysics == false) {
			this.isOnGround = onGround;
			return;
		}

		World world = this.getWorld();
		double x = this.getLocation().getX();
		double y = this.getLocation().getY();
		double z = this.getLocation().getZ();
		BlockId id1 = world.getBlock(floor(x), floor(y), floor(z)).getBlock();
		BlockId id2 = world.getBlock(floor(x + (getWidth() / 2)), floor(y), floor(z)).getBlock();
		BlockId id3 = world.getBlock(floor(x - (getWidth() / 2)), floor(y), floor(z)).getBlock();
		BlockId id4 = world.getBlock(floor(x), floor(y), floor(z - (getWidth() / 2))).getBlock();
		BlockId id5 = world.getBlock(floor(x), floor(y), floor(z + (getWidth() / 2))).getBlock();
		BlockId[] array = new BlockId[] { id1, id2, id3, id4, id5 };
		boolean isInWater = compare(BlockId.WATER, array) || compare(BlockId.WATER_FLOWING, array);

		if (isInWater) {
			// reset fall damage
			this.yLocation = this.getLocation().getY();
		}
		if (this.isOnGround == false && onGround == true) {
			if (this.clientSettings.isFlying) {
				return; // Flying players don't get damage in vanilla
			}
			// fall damage
			double change = this.yLocation - this.getLocation().getY() - 3;
			if (change > 0) {
				if (isInWater) {
					// nothing should happen
				} else {
					if (damage((float) change)) {
						Main.getInstance().broadcastMessage(new SimpleMessage(this.getPlayerName() + " fell from a high place."));
					}
					Main.getInstance().getWorld(this).broadcastSound("damage.fallsmall", 1F, (byte) 63, getLocation(), null);
				}
			}
		} else if (this.isOnGround == true && onGround == false) {
			// save Y location
			this.yLocation = this.getLocation().getY();
		}
		this.isOnGround = onGround;
	}

	private boolean compare(BlockId fire, BlockId[] ids) {
		for (BlockId id : ids) {
			if (id == fire) {
				return true;
			}
		}
		return false;
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
		if (this.damage(1F, Vector.substract(attacker.getLocation(), this.getLocation()).normalize(this.getLocation().distance(attacker.getLocation()) * 2))) {
			Main.getInstance().broadcastMessage(new SimpleMessage(this.getPlayerName() + " was killed by " + attacker.getPlayerName()));
		}
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
	public void kick(Message reason) {
		PlayerKickEvent e = new PlayerKickEvent(this, reason);
		if (e.isCancelled()) {
			return;
		}
		if (e.getReason() == null) {
			this.getNetworkManager().disconnect(reason, false);
		} else {
			this.getNetworkManager().disconnect(e.getReason(), false);
		}
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
		if (Main.getInstance().callEvent(new PlayerExpChangeEvent(this, this.getExperience(), experience))) {
			return;
		}

		this.clientSettings.experience = experience;
		this.networkManager.sendPacket(new PacketOutSetExperience(0F, this.clientSettings.experience, this.clientSettings.experience)); // TODO convert this to a level
	}

	@Override
	public void setExperienceLevel(int experienceLevel) {
		if (Main.getInstance().callEvent(new PlayerExpChangeEvent(this, this.getExperience(), experienceLevel))) { // TODO convert this to a level
			return;
		}

		this.clientSettings.experience = experienceLevel; // TODO convert this to a level
		this.networkManager.sendPacket(new PacketOutSetExperience(0F, this.clientSettings.experience, this.clientSettings.experience)); // TODO calculate this correctly
	}

	@Override
	public boolean canSprint() {
		return this.clientSettings.food >= 6;
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
		if (Main.getInstance().callEvent(new PlayerToggleSneakEvent(this, sneaking))) {
			return;
		}
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
		if (Main.getInstance().callEvent(new PlayerGamemodeChangeEvent(this, this.getGameMode(), mode))) {
			return;
		}

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

	@Override
	public boolean isSprinting() {
		return this.clientSettings.isSprinting;
	}

	@Override
	public boolean isOnFire() {
		return getFireTicks() > 0;
	}

	@Override
	public boolean isEating() {
		return this.clientSettings.isEatingTicks > 0;
	}

	@Override
	protected String getRandomSound() {
		return "";
	}

	@Override
	public void setSprinting(boolean sprinting) {
		if (Main.getInstance().callEvent(new PlayerToggleSprintEvent(this, sprinting))) {
			return;
		}
		this.clientSettings.isSprinting = sprinting;
		this.updateDataWatcher();
		this.getWorld().broadcastPacket(new PacketOutEntityMetadata(this.getEntityId(), this.getDataWatcher()), this.getLocation());
	}

	@Override
	public void displayWelcomeTitle(Message title, Message subtitle) {
		this.networkManager.sendPacket(new PacketOutTitle(new ActionDisplayTitle(title)));
		this.networkManager.sendPacket(new PacketOutTitle(new ActionSubtitle(subtitle)));
	}

	@Override
	public float getWidth() {
		return 0.6F;
	}

	@Override
	public float getHeight() {
		return 1.68F;
	}

	@Override
	public Iterator<Location> getLineOfSight(double range, int blocksPerLocation) {
		List<Location> list = new ArrayList<>();
		Location startLocation = this.getLocation().add(0, 1, 0);
		Location endLocation = this.getTargetBlock(range);
		double xDiff = endLocation.getX() - startLocation.getX();
		double yDiff = endLocation.getY() - startLocation.getY();
		double zDiff = endLocation.getZ() - startLocation.getZ();

		double xScale = xDiff / (blocksPerLocation * range);
		double yScale = yDiff / (blocksPerLocation * range);
		double zScale = zDiff / (blocksPerLocation * range);

		double currentX = 0;
		double currentY = 0;
		double currentZ = 0;

		for (int i = 0; i < (blocksPerLocation * range); i++) {
			currentX += xScale;
			currentY += yScale;
			currentZ += zScale;
			list.add(new Location(endLocation.getWorld(), currentX + startLocation.getX(), currentY + startLocation.getY(), currentZ + startLocation.getZ(), 0D, 0D));
		}
		return list.iterator();
	}

	@Override
	public Location getTargetBlock(double range) {
		double yaw = this.getHeadLocation().getYaw();
		double pitch = this.getHeadLocation().getPitch();

		boolean watchingUp = false;
		boolean watchingNorth = false;
		boolean watchingEast = false;
		boolean watchingSouth = false;
		boolean watchingWest = false;

		if (pitch >= 0) {
			watchingUp = true;
		} else if (pitch < 0) {
			pitch *= -1;
		}

		while (yaw > 360) {
			yaw -= 360;
		}
		while (yaw < -360) {
			yaw += 360;
		}

		if (isInBetween(yaw, -360, -270)) {
			yaw *= -1;
			yaw -= 270;
			yaw = 90 - yaw;
			watchingNorth = true;
		} else if (isInBetween(yaw, -270, -180)) {
			yaw *= -1;
			yaw -= 180;
			yaw = 90 - yaw;
			watchingEast = true;
		} else if (isInBetween(yaw, -180, -90)) {
			yaw *= -1;
			yaw -= 90;
			yaw = 90 - yaw;
			watchingSouth = true;
		} else if (isInBetween(yaw, -90, 0)) {
			yaw *= -1;
			yaw = 90 - yaw;
			watchingWest = true;
		} else if (isInBetween(yaw, 0, 90)) {
			watchingNorth = true;
		} else if (isInBetween(yaw, 90, 180)) {
			yaw -= 90;
			watchingEast = true;
		} else if (isInBetween(yaw, 180, 270)) {
			yaw -= 180;
			watchingSouth = true;
		} else if (isInBetween(yaw, 270, 360)) {
			yaw -= 270;
			watchingWest = true;
		}

		double radianYaw = Math.toRadians(yaw);
		double radianPitch = Math.toRadians(pitch);

		double yTranslation = Math.sin(radianPitch) * range;
		double hozTranslation = Math.cos(radianPitch) * range;
		double xTranslation = Math.cos(radianYaw) * hozTranslation;
		double zTranslation = Math.sin(radianYaw) * hozTranslation;

		Location old = this.getLocation();
		if (watchingNorth) {
			return new Location(old.getWorld(), old.getX() - zTranslation, (watchingUp ? (old.getY() - yTranslation) : (old.getY() + yTranslation)), old.getZ() + xTranslation, yaw, pitch);
		} else if (watchingEast) {
			return new Location(old.getWorld(), old.getX() - xTranslation, (watchingUp ? (old.getY() - yTranslation) : (old.getY() + yTranslation)), old.getZ() - zTranslation, yaw, pitch);
		} else if (watchingSouth) {
			return new Location(old.getWorld(), old.getX() + zTranslation, (watchingUp ? (old.getY() - yTranslation) : (old.getY() + yTranslation)), old.getZ() - xTranslation, yaw, pitch);
		} else if (watchingWest) {
			return new Location(old.getWorld(), old.getX() + xTranslation, (watchingUp ? (old.getY() - yTranslation) : (old.getY() + yTranslation)), old.getZ() + zTranslation, yaw, pitch);
		}
		throw new RuntimeException("This shouldn't happen! PITCH: " + pitch + " YAW: " + yaw + " North: " + watchingNorth + " East: " + watchingEast + " South: " + watchingSouth + " West: " + watchingWest);
	}

	private static boolean isInBetween(double yaw, int from, int to) {
		return yaw >= from && yaw <= to;
	}
}
