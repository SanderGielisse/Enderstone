/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.enderstone.server.regions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.regions.generators.TimTest;

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
	public static final int AMOUNT_OF_CHUNKSECTIONS = 16;

	public EnderChunk getOrCreateChunk(int x, int z) {
		EnderChunk r = getChunk(x, z);
		if (r != null) {
			return r;
		}
		BlockId[][] blocks = generator.generateExtBlockSections(this, new Random(), x, z, null);
		if (blocks == null) {
			EnderLogger.warn("Generator " + generator.toString() + " returned null for chunk " + x + ", " + z);
			blocks = new BlockId[AMOUNT_OF_CHUNKSECTIONS][];
		}
		if (blocks.length != AMOUNT_OF_CHUNKSECTIONS) {
			EnderLogger.warn("Generator " + generator.toString() + " returned invalid size for chunk " + x + ", " + z);
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
		return checkChunkPopulation(r);
	}

	public EnderChunk getChunk(int x, int z) {
		EnderChunk loaded = this.loadChunk(x, z);
		if (loaded != null) {
			return loaded;
		}
		return checkChunkPopulation(this.loadedChunks.get(x, z));
	}

	public EnderChunk loadChunk(int x, int z) {
		return checkChunkPopulation(null);
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
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					if (k == 0 && i == 0) {
						continue;
					}
					EnderChunk found = this.getChunk(i + c.getX(), k + c.getZ());
					if (found == null) {
						return c;
					}
				}
			}
			didPopulate = true;
			try {
				for (BlockPopulator blocks : generator.getDefaultPopulators(this)) {
					blocks.populate(this, random, c);
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

	public byte getBlockDataAt(int x, int y, int z) {
		return getOrCreateChunk(x >> 4, z >> 4).getData(x & 0xF, y & 0xFF, z & 0xF);
	}

	public void setBlockAt(int x, int y, int z, BlockId id, byte data) {
		getOrCreateChunk(x >> 4, z >> 4).setBlock(x & 0xF, y & 0xFF, z & 0xF, id, data);
	}

	public void doChunkUpdatesForPlayer(final EnderPlayer player, final ChunkInformer informer, final int radius) throws Exception {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {

				if (players.get(player) == null) {
					players.put(player, new RegionSet());
				}

				Set<EnderChunk> playerChunks = players.get(player);
				int tmp1 = radius * 2 + 1;
				int x = (player.getLocation().getBlockX() >> 4) - radius;
				int maxX = x + tmp1;
				int z = (player.getLocation().getBlockZ() >> 4) - radius;
				int minZ = z;
				int maxZ = z + tmp1;
				if (playerChunks.isEmpty()) {

					while (x++ < maxX) {
						for (z = minZ; z < maxZ; z++) {
							EnderChunk c = getOrCreateChunk(x, z);
							playerChunks.add(c);
							try {
								if (!informer.sendChunk(c)) {
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					int[][] chunkLoad = new int[(radius * 2) * (radius * 2) * 2][];
					int index = 0;
					Set<EnderChunk> copy = new HashSet<>(playerChunks);

					for (; x < maxX; x++) {
						for (z = minZ; z < maxZ; z++) {
							EnderChunk tmp = getOrCreateChunk(x, z);
							if (!copy.contains(tmp)) {
								chunkLoad[index++] = new int[] { x, z };
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

					index = 0;
					for (int[] i : chunkLoad) {
						if (i == null) {
							break;
						}
						x = i[0];
						z = i[1];
						EnderChunk c = getOrCreateChunk(x, z);
						playerChunks.add(c);
						try {
							if (!informer.sendChunk(c)) {
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						index++;
					}
				}
			}
		});
	}

	public long getSeed() {
		if (seed == null) {
			seed = Main.random.nextLong();
		}
		return seed;
	}

	public static interface ChunkInformer {

		public boolean sendChunk(EnderChunk chunk) throws Exception;

		public boolean removeChunk(EnderChunk chunk);
	}
}
