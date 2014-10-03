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
package org.enderstone.server.regions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.api.Block;
import org.enderstone.server.api.Chunk;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Entity;
import org.enderstone.server.api.entity.Item;
import org.enderstone.server.api.entity.Mob;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.EntityItem;
import org.enderstone.server.entity.EntitySpider;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.regions.generators.MultiChunkBlockPopulator;
import org.enderstone.server.regions.tileblocks.TileBlock;
import org.enderstone.server.regions.tileblocks.TileBlocks;
import org.enderstone.server.util.IntegerArrayComparator;

public class EnderWorld implements World{

	private Long seed = null;
	private final RegionSet loadedChunks = new RegionSet();
	private final ChunkGenerator generator;
	private final Random random = new Random();
	private long time = random.nextInt();
	public static final int AMOUNT_OF_CHUNKSECTIONS = 16;
	public final Set<EnderEntity> entities = new HashSet<>();
	public final Set<EnderPlayer> players = new HashSet<>();
	public final List<TileBlock> tickList = new ArrayList<>();
	private Location spawnLocation;
	public final String worldName;

	public EnderWorld(String worldName, ChunkGenerator gen) {
		this.worldName = worldName;
		this.generator = gen;
	}

	public EnderChunk getOrCreateChunk(int x, int z) {
		return getOrCreateChunk(x, z, true);
	}

	private EnderChunk getOrCreateChunk(int x, int z, boolean checkChunkPopulation) {		
		EnderChunk c = this.getOrCreateChunk0(x, z);
		return checkChunkPopulation ? checkChunkPopulation(c) : c;
	}

	private EnderChunk getOrCreateChunk0(int x, int z) {
		EnderChunk r = getChunk(x, z, false);
		if (r != null) {
			return r;
		}
		BlockId[][] blocks = generator.generateExtBlockSections(this, new Random(), x, z);
		if (blocks == null) {
			blocks = new BlockId[AMOUNT_OF_CHUNKSECTIONS][];
		}
		if (blocks.length != AMOUNT_OF_CHUNKSECTIONS) {
			blocks = new BlockId[AMOUNT_OF_CHUNKSECTIONS][];
		}
		short[][] id = new short[16][];
		byte[][] data = new byte[16][];
		for (int i = 0; i < blocks.length; i++) {

			if (blocks[i] != null) {
				id[i] = new short[4096];
				data[i] = new byte[4096];
				for (int j = 0; j < 4096; j++) {

					if (blocks[i][j] == null) {
						id[i][j] = BlockId.AIR.getId();
					} else {
						id[i][j] = blocks[i][j].getId();
					}
				}
			}
		}
		loadedChunks.add(r = new EnderChunk(this, x, z, id, data, new byte[16 * 16], new ArrayList<BlockData>()));
		return r;
	}

	public EnderChunk getChunk(int x, int z) {
		return getChunk(x, z, true);
	}

	private EnderChunk getChunk(int x, int z, boolean checkChunkPopulation) {
		EnderChunk c = this.getChunk0(x, z);
		return checkChunkPopulation ? checkChunkPopulation(c) : c;
	}

	private EnderChunk getChunk0(int x, int z) {
		return this.loadedChunks.get(x, z);

	}

	public void saveChunk(EnderChunk ender) {

	}

	private EnderChunk checkChunkPopulation(EnderChunk c) {
		if (c == null || c.hasPopulated) {
			return c;
		}
		boolean didPopulate = false;
		try {
			c.hasPopulated = true;
			EnderChunk[] array = new EnderChunk[9];
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					if (k == 0 && i == 0) {
						continue;
					}
					EnderChunk found = this.getOrCreateChunk(i + c.getX(), k + c.getZ(), false);
					if (found == null) {
						return c;
					}
					array[(i+1)+(k+1)*3] = found;
				}
			}
			array[(0+1)+(0+1)*3] = c;
			didPopulate = true;
			try {
				for (MultiChunkBlockPopulator blocks : generator.getDefaultPopulators(this)) {
					blocks.populate(this, random, new MultiChunkBlockPopulator.ChunkGrid(array));
				}
			} catch (Exception e) {
				EnderLogger.exception(e);
			}
		} finally {
			c.hasPopulated = didPopulate;
		}
		return c;
	}

	public BlockId getBlockIdAt(int x, int y, int z) {
		return getOrCreateChunk(x >> 4, z >> 4).getBlock(x & 0xF, y & 0xFF, z & 0xF);
	}
	
	public BlockId getBlockIdAt(Location loc) {
		return getOrCreateChunk(loc.getBlockX() >> 4, loc.getBlockZ() >> 4).getBlock(loc.getBlockX() & 0xF, loc.getBlockY() & 0xFF, loc.getBlockZ() & 0xF);
	}

	public byte getBlockDataAt(int x, int y, int z) {
		return getOrCreateChunk(x >> 4, z >> 4).getData(x & 0xF, y & 0xFF, z & 0xF);
	}

	public void setBlockAt(int x, int y, int z, BlockId id, byte data) {
		getOrCreateChunk(x >> 4, z >> 4).setBlock(x & 0xF, y & 0xFF, z & 0xF, id, data);
	}

	public void doChunkUpdatesForPlayer(EnderPlayer player, ChunkInformer informer, int radius) {
		doChunkUpdatesForPlayer(player, informer, radius, false);
	}

	public void doChunkUpdatesForPlayer(EnderPlayer player, ChunkInformer informer, int radius, boolean force) {
		synchronized (informer) {
			RegionSet playerChunks = player.getLoadedChunks();
			List<EnderChunk> newPlayerChunks = new ArrayList<>();
			int r2 = radius * 2 + 1;
			int px = player.getLocation().getBlockX() >> 4;
			int cx = (px) - radius;
			int mx = cx + r2;
			int pz = player.getLocation().getBlockZ() >> 4;
			int minz = (pz) - radius;
			int cz = minz;
			int mz = cz + r2;
			try {
				if (playerChunks.isEmpty()) {
					while (cx++ < mx) {
						for (cz = minz; cz < mz; cz++) {
							EnderChunk c = getOrCreateChunk(cx, cz);
							playerChunks.add(c);
							informer.sendChunk(c);
						}
					}
				} else {
					int maxSize = force ? Integer.MAX_VALUE : informer.maxChunks();
					int[][] chunkLoad = new int[(radius * 2) * (radius * 2) * 2][];
					int index = 0;
					Set<EnderChunk> copy = new RegionSet(playerChunks);

					for (; cx < mx; cx++) {
						for (cz = minz; cz < mz; cz++) {
							EnderChunk tmp = getOrCreateChunk(cx, cz);
							if (!copy.contains(tmp)) {
								chunkLoad[index++] = new int[]{cx, cz};
							} else {
								copy.remove(tmp);
							}
						}
					}
					Iterator<EnderChunk> loop = copy.iterator();
					while (loop.hasNext()) {
						EnderChunk i = loop.next();

						playerChunks.remove(i);
						informer.removeChunk(i);
					}
					Arrays.sort(chunkLoad, 0, index, new IntegerArrayComparator(px, pz));
					
					if (maxSize < chunkLoad.length) chunkLoad[maxSize] = null;
					index = 0;
					for (int[] load : chunkLoad) {
						if (load == null) {
							break;
						}
						cx = load[0];
						cz = load[1];
						EnderChunk c = getOrCreateChunk(cx, cz);
						newPlayerChunks.add(c);
						informer.sendChunk(c);
						index++;
					}
				}
			} finally {
				informer.done();
				playerChunks.addAll(newPlayerChunks);
			}
		}
	}

	public long getSeed() {
		if (seed == null) {
			seed = Main.random.nextLong();
		}
		return seed;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	public static interface ChunkInformer {

		public void sendChunk(EnderChunk chunk);

		public void removeChunk(EnderChunk chunk);

		public void done();

		public int maxChunks();
	}

	public void broadcastSound(String soundName, float volume, byte pitch, Location loc, EnderPlayer exceptOne) {
		PacketOutSoundEffect packet = new PacketOutSoundEffect(soundName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), volume, pitch);
		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if ((!ep.equals(exceptOne)) && loc.isInRange(15, ep.getLocation(), true)) {
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}
	
	private final List<EnderEntity> pendingEntities = new ArrayList<>();

	public void addEntity(EnderEntity e) {
		this.pendingEntities.add(e);
	}
	
	public void removeEntity(EnderEntity e, boolean broadcastRemove){
		if(this.entities.contains(e)){
			this.entities.remove(e);
		}
		if (broadcastRemove) {
			this.broadcastPacket(new PacketOutEntityDestroy(new Integer[] { e.getEntityId() }), e.getLocation());
			for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
				Iterator<EnderEntity> it = ep.canSeeEntity.iterator();
				while (it.hasNext()) {
					EnderEntity et = it.next();
					if (et.equals(e)) {
						it.remove();
					}
				}
			}
		}
	}

	public void updateEntities(Set<EnderPlayer> onlinePlayers) {
		for (EnderEntity e : this.entities) {
			e.updatePlayers(onlinePlayers);
		}
	}

	public Block getBlock(int x, int y, int z) {
		return new EnderBlock(x, y, z, this);
	}

	public Location getSpawn() {
		if(this.spawnLocation == null){
			this.spawnLocation = new Location(this, 0.5, getChunkAt(0, 0).getHighestBlockAt(0, 0) + 1, 0.5, 0F, 0F);
		}
		return this.spawnLocation;
	}

	public void serverTick() {
		for(EnderEntity pending : this.pendingEntities){
			this.entities.add(pending);
			pending.onSpawn();
		}
		this.pendingEntities.clear();
		
		Iterator<EnderEntity> it = this.entities.iterator();
		while(it.hasNext()){
			EnderEntity e = it.next();
			if (e.shouldBeRemoved()) {
				it.remove();
				if(e.shouldBroadcastDespawn()){
					if (e.getWorld() instanceof EnderWorld) {
						((EnderWorld) e.getWorld()).broadcastPacket(new PacketOutEntityDestroy(new Integer[] { e.getEntityId() }), e.getLocation());
					}
				}
			}
			e.serverTick();
		}
		if (this.time % 10 == 0) {
			List<TileBlock> copiedList = new ArrayList<>(tickList);
			for (TileBlock tile : copiedList) {
				boolean shouldRemove = tile.serverTick();
				if (shouldRemove) {
					this.tickList.remove(tile);
				}
			}
		}
		this.time += 1;
	}
	
	public void doTileBlock(int x, int y, int z) {
		EnderChunk chunk = this.getChunk(x >> 4, z >> 4, false);
		if(chunk == null){
			return;
		}
		BlockId blockId = chunk.getBlock(x & 0xF, y & 0xFF, z & 0xF);
		Class<? extends TileBlock> clazz = TileBlocks.getTileBlock(blockId);
		if(clazz != null){
			for(TileBlock tile : this.tickList){
				if(tile.getX() == x && tile.getY() == y && tile.getZ() == z){
					//already is a tile block
					return;
				}
			}
			try {
				this.tickList.add(clazz.getConstructor(EnderWorld.class, int.class, int.class, int.class).newInstance(this, x, y, z));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void broadcastPacket(Packet packet, Location loc){
		for(EnderPlayer ep : Main.getInstance().onlinePlayers){
			if(ep.getLocation().isInRange(35, loc, true)){
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}
	
	public void broadcastPacket(Packet packet, Location loc, EnderPlayer skipPlayer){
		for(EnderPlayer ep : Main.getInstance().onlinePlayers){
			if(ep.getLocation().isInRange(35, loc, true) && !ep.equals(skipPlayer)){
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}

	@Override
	public Collection<? extends Chunk> getLoadedChunks() {
		return this.loadedChunks;
	}

	@Override
	public Item dropItem(Location location, ItemStack itemStack, int noPickupDelay) {
		EnderEntity entity;
		this.addEntity(entity = new EntityItem(this, location.clone().add(0, 1D, 0), itemStack, noPickupDelay, new Vector(0, 0.1D, 0)));
		return (Item) entity;
	}

	@Override
	public void strikeLightning(Location location) {
		// TODO
	}

	@Override
	public Collection<? extends Entity> getEntities() {
		return this.entities;
	}

	@Override
	public Collection<? extends Player> getPlayers() {
		return this.players;
	}

	@Override
	public Collection<? extends Mob> getMobs() {
		List<Mob> mobs = new ArrayList<>();
		for(Entity e : this.getEntities()){
			if(e instanceof Mob){
				mobs.add((Mob) e);
			}
		}
		return mobs;
	}

	@Override
	public String getName() {
		return this.worldName;
	}

	@Override
	public Block getBlock(Location location) {
		return this.getBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	@Override
	public Chunk getChunkAt(int x, int z) {
		return getOrCreateChunk(x, z);
	}
	
	@Override
	public void playSound(Location location, String soundName, float volume, int pitch) {
		this.broadcastPacket(new PacketOutSoundEffect(soundName, location.getBlockX(), location.getBlockY(), location.getBlockZ(), volume, (byte) pitch), location);
	}
}
