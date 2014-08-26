/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.bigteddy98.mcserver.regions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.bigteddy98.mcserver.entity.EnderPlayer;

/**
 *
 * @author Fernando
 */
public class EnderWorld {

	private final RegionSet loadedChunks = new RegionSet();
	private final Map<EnderPlayer, RegionSet> players = new LinkedHashMap<>();
	private final ChunkGenerator generator = new ChunkGenerator() {

		@Override
		public BlockId[][] generateExtBlockSections(EnderWorld world, Random random, int x, int z, ChunkGenerator.BiomeGrid biomes) {
			BlockId[][] r = new BlockId[16][];
			r[0] = new BlockId[4096];
			for (int i = 0; i < r[0].length; i++) {
				r[0][i] = BlockId.grass;
			}
			return r;
		}
	};

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
						id[i][j] = blocks[i][j].getId();
					}
				}
			}
			loadedChunks.add(r = new EnderChunk(x, z, id, data, new byte[16 * 16], new ArrayList<BlockData>()));
		}
		return r;

	}

	public void doChunkUpdatesForPlayer(EnderPlayer player, ChunkInformer informer, int radius) throws Exception {

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
				this.loadedChunks.remove(i);
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
			if (index != 0) {
				System.out.println("Sent " + index + " chunks to player " + this);
			}
			System.out.println("Now " + playerChunks.size() + " chunks loaded at player " + this);
		}
	}

	public static interface ChunkInformer {

		public boolean sendChunk(EnderChunk chunk) throws Exception;

		public boolean removeChunk(EnderChunk chunk);
	}
}
