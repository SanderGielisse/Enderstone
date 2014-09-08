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
package org.enderstone.server.regions.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.generators.util.SimplexOctaveGenerator;

/**
 *
 * @author Fernando
 */
public class FlyingIslandsGenerator implements ChunkGenerator {

	@Override
	public BlockId[][] generateExtBlockSections(EnderWorld world, Random random, int ChunkX, int ChunkZ) {
		BlockId[][] chunk = new BlockId[16][];

		SimplexOctaveGenerator overhangs = new SimplexOctaveGenerator(world, 8);
		SimplexOctaveGenerator bottoms = new SimplexOctaveGenerator(world, 8);
		SimplexOctaveGenerator oceanGen = new SimplexOctaveGenerator(world, 8);
		SimplexOctaveGenerator sandGen = new SimplexOctaveGenerator(world, 8);

		overhangs.setScale(1 / 64.0); // little note: the .0 is VERY important
		bottoms.setScale(1 / 256.0);
		oceanGen.setScale(0.03125D);
		sandGen.setScale(1 / 16.0);

		int overhangsMagnitude = 32; // used when we generate the noise for the
										// tops of the overhangs
		int bottomsMagnitude = 16;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = x + ChunkX * 16;
				int realZ = z + ChunkZ * 16;
				// biomes.setBiome(x, z, Biome.OCEAN);

				// int bottomHeight = 32;
				int bottomHeight = (int) (bottoms.noise(realX, realZ, 0.6, 0.5) * bottomsMagnitude + 64) + 16;
				int maxHeight = (int) overhangs.noise(realX, realZ, 0.5, 0.6) * overhangsMagnitude + bottomHeight + 32;
				double seaNoise = (oceanGen.noise(realX, 48, realZ, 0.5D, 0.5D, true) + 1) * 13;
				double sandNoise = Double.NaN;

				int floodHeight = 48 + (int) Math.round(seaNoise);

				for (int y = 0; y < maxHeight; y++) {
					if (y <= floodHeight) {
						setBlock(x, y, z, chunk, BlockId.STONE);
					} else if (y > bottomHeight) { // part where we do the
													// overhangs
						double density = overhangs.noise(realX, y, realZ, 0.5, 0.5);

						if (density > 0.3 && density < 0.7) {
							setBlock(x, y, z, chunk, BlockId.STONE);
							// biomes.setBiome(x, z, Biome.JUNGLE);
						} else if (y < 80) {
							setBlock(x, y, z, chunk, BlockId.WATER);
						}
					} else if (y < 80) {
						setBlock(x, y, z, chunk, BlockId.WATER);
					}

				}

				for (int y = bottomHeight + 1; y < maxHeight; y++) {
					BlockId thisblock = getBlock(x, y, z, chunk);
					BlockId blockabove = getBlock(x, y + 1, z, chunk);

					if (thisblock == BlockId.STONE) {
						if (blockabove == BlockId.AIR || blockabove == null) {
							if (random.nextInt(8) == 0) {
								if (random.nextBoolean()) {
									// setBlock(x, y + 1, z, chunk,
									// Material.YELLOW_FLOWER);
								} else {
									// setBlock(x, y + 1, z, chunk,
									// Material.RED_ROSE);
								}

							}
							setBlock(x, y, z, chunk, BlockId.GRASS);
							if (getBlock(x, y - 1, z, chunk) == BlockId.STONE) {
								setBlock(x, y - 1, z, chunk, BlockId.DIRT);
								if (getBlock(x, y - 2, z, chunk) == BlockId.STONE) {
									setBlock(x, y - 2, z, chunk, BlockId.DIRT);
									if (getBlock(x, y - 3, z, chunk) == BlockId.STONE) {
										setBlock(x, y - 3, z, chunk, BlockId.DIRT);
									}
								}
							}

						}
						if (blockabove == BlockId.WATER) {
							setBlock(x, y, z, chunk, BlockId.SAND);
							setBlock(x, y - 1, z, chunk, BlockId.SAND);
							sandNoise = (sandGen.noise(realX, seaNoise, realZ, 0.5D, 0.5D, true) + 1) * 1.5;
							for (int i = 0; i < sandNoise; i++) {
								setBlock(x, y - 2 - i, z, chunk, BlockId.SANDSTONE);
							}

						}
					}
				}
				BlockId thisblock = getBlock(x, floodHeight, z, chunk);
				BlockId blockabove = getBlock(x, floodHeight + 1, z, chunk);
				if (thisblock == BlockId.STONE && blockabove == BlockId.WATER) {
					setBlock(x, floodHeight, z, chunk, BlockId.SAND);
					setBlock(x, floodHeight - 1, z, chunk, BlockId.SAND);
					if (sandNoise == Double.NaN) {
						sandNoise = (sandGen.noise(realX, seaNoise, realZ, 0.5D, 0.5D, true) + 1) * 1.5;
					}
					for (int i = 0; i < sandNoise; i++) {
						setBlock(x, floodHeight - 2 - i, z, chunk, BlockId.SANDSTONE);
					}
				}
			}
		}
		return chunk;
	}

	private void setBlock(int x, int y, int z, BlockId[][] chunk, BlockId material) {
		if (chunk[y >> 4] == null) {
			chunk[y >> 4] = new BlockId[16 * 16 * 16];
		}
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			return;
		}
		chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = material;
	}

	private BlockId getBlock(int x, int y, int z, BlockId[][] chunk) {
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			return BlockId.AIR;
		}
		if (chunk[y >> 4] == null) {
			return BlockId.AIR;
		}
		BlockId id = chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
		if(id == null){
			id = BlockId.AIR;
		}
		return id;
	}

	@Override
	public List<MultiChunkBlockPopulator> getDefaultPopulators(EnderWorld world) {
		return new ArrayList<>();
	}
}