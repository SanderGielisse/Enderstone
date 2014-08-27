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

import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.regions.generators.FlyingIslandsGenerator;

/**
 *
 * @author Fernando
 */
public class EnderWorld {

	private Long seed = null;
	private final RegionSet loadedChunks = new RegionSet();
	public final Map<EnderPlayer, RegionSet> players = new LinkedHashMap<>();
	private final ChunkGenerator generator = new FlyingIslandsGenerator();

	public EnderChunk getOrCreateChunk(int x, int z) {
		EnderChunk r = loadedChunks.get(x, z);
		if (r == null) {
			BlockId[][] blocks = generator.generateExtBlockSections(this, new Random(), x, z, null);

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
		}
		return r;
	}

	public synchronized void doChunkUpdatesForPlayer(EnderPlayer player, ChunkInformer informer, int radius) throws Exception {

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
					if (!informer.sendChunk(c)) {
						return;
					}
				}
			}
		} else {
			int[][] chunkLoad = new int[(radius * 2) * (radius * 2) * 2][];
			int index = 0;
			Set<EnderChunk> copy = new HashSet<>(playerChunks);
			System.out.println("COPY SIZE: " + copy.size());

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
				if (!informer.sendChunk(c)) {
					return;
				}
				index++;
			}
		}
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
