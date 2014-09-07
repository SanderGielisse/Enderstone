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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.Entity;
import org.enderstone.server.entity.EntityItem;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.regions.generators.MultiChunkBlockPopulator;
import org.enderstone.server.regions.generators.TimTest;
import org.enderstone.server.util.IntegerArrayComparator;

/**
 *
 * @author Fernando
 */
public class EnderWorld {

	private Long seed = null;
	private final RegionSet loadedChunks = new RegionSet();
	public final Map<EnderPlayer, RegionSet> players = new LinkedHashMap<>();
	private final ChunkGenerator generator = new TimTest();
	private final Random random = new Random();
	private long time = random.nextInt();
	public static final int AMOUNT_OF_CHUNKSECTIONS = 16;
	public final Set<Entity> entities = new HashSet<>();
	private Location spawnLocation = new Location(null, 0, 80, 0, 0f, 0f);

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
		loadedChunks.add(r = new EnderChunk(x, z, id, data, new byte[16 * 16], new ArrayList<BlockData>()));
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
			RegionSet playerChunks = players.get(player);
			List<EnderChunk> newPlayerChunks = new ArrayList<>();
			if (playerChunks == null) {
				players.put(player, playerChunks = new RegionSet());
			}
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
	
	public Entity dropItem(ItemStack item, Location loc, int noPickupDelay)
	{
		Entity entity;
		this.addEntity(entity = new EntityItem(loc.clone(), item, noPickupDelay));
		return entity;
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

	public void addEntity(Entity e) {
		this.entities.add(e);
	}
	
	public void removeEntity(Entity e){
		if(this.entities.contains(e)){
			this.entities.remove(e);
			this.broadcastPacket(new PacketOutEntityDestroy(new Integer[] {e.getEntityId()}), e.getLocation());
		}
	}

	public void updateEntities(List<EnderPlayer> onlinePlayers) {
		for (Entity e : this.entities) {
			e.updatePlayers(onlinePlayers);
		}
	}

	public Block getBlock(int x, int y, int z) {
		return new EnderBlock(x, y, z, this);
	}

	public Location getSpawn() {
		return this.spawnLocation;
	}

	public void setSpawn(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public void serverTick()
	{
		for(Entity e : this.entities){
			e.serverTick();
		}
		this.time += 1;
	}
	
	public void broadcastPacket(Packet packet, Location loc){
		for(EnderPlayer ep : Main.getInstance().onlinePlayers){
			if(ep.getLocation().isInRange(35, loc, true)){
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}
}
