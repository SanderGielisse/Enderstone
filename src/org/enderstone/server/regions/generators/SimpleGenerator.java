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
import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.entity.EntitySpider;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.BlockPopulator;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.generators.util.SimplexOctaveGenerator;

public class SimpleGenerator implements ChunkGenerator {

	@Override
	public BlockId[][] generateExtBlockSections(EnderWorld world, Random random, int i, int o) {
		BlockId[][] chunk = new BlockId[16][];

		SimplexOctaveGenerator land = new SimplexOctaveGenerator(world, 8);
		land.setScale(1 / 64.0);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = x + i * 16;
				int realZ = z + o * 16;
				double frequency = 0.5;
				double amplitude = 0.5;
				int multitude = 10;
				int sea_level = 64;

				double maxHeight = land.noise(realX, realZ, frequency, amplitude) * multitude + sea_level;
				int timer = 0;
				for (double y = maxHeight; y > 0; y--) {
					BlockId block = null;
					if (timer < 1) {
						block = BlockId.GRASS;
					} else if (timer < 3) {
						if (timer == 2) {
							if (random.nextBoolean()) {
								block = BlockId.DIRT;
							} else {
								block = BlockId.STONE;
							}
						} else {
							block = BlockId.DIRT;
						}
					} else {
						block = BlockId.STONE;
					}
					if (block != null) {
						setBlock(x, (int) y, z, chunk, block);
					}
					timer++;
				}
			}
		}
		return chunk;
	}

	@Override
	public List<MultiChunkBlockPopulator> getDefaultPopulators(EnderWorld world) {
		List<MultiChunkBlockPopulator> pops = new ArrayList<>();

		pops.add(new BlockPopulator() {

			public void setBlock(int x, int z, EnderChunk source) {
				source.setBlock(x, source.getHighestBlockAt(x, z) + 1, z, BlockId.RED_ROSE, (byte) 0);
			}

			@Override
			public void populate(EnderWorld world, Random random, EnderChunk source) {
				setBlock(1, 0, source);
				setBlock(0, 1, source);

				setBlock(14, 15, source);
				setBlock(15, 14, source);

				setBlock(1, 15, source);
				setBlock(0, 14, source);

				setBlock(15, 1, source);
				setBlock(14, 0, source);
				
				//TEMPORARILY TESTS
				world.addEntity(new EntitySpider(world, new Location(world, source.getX() * 16, source.getHighestBlockAt(0, 0) + 1, source.getZ() * 16, 0F, 0F)));
				source.setBlock(8, source.getHighestBlockAt(8, 0), 0, BlockId.WATER, (byte) 0);
				source.setBlock(9, source.getHighestBlockAt(9, 0), 0, BlockId.FIRE, (byte) 0);
				
				int x = random.nextInt(5) + 3, z = random.nextInt(5) + 3;
				try {
					generateTree(source, x, source.getHighestBlockAt(x, z), z);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return pops;
	}

	private void generateTree(EnderChunk chunk, int x, int y, int z) {
		int height = Main.random.nextInt(3) + 5;
		
		for(int i = 0; i < height; i++){
			chunk.setBlock(x, -1 + i + y, z, BlockId.LOG, (byte) 0);
		}
		
		for(int leafY = height - 2; leafY < height + 1; leafY++){
			for(int leafX = -1; leafX < 2; leafX++){
				for(int leafZ = -1; leafZ < 2; leafZ++){
					chunk.setBlock(leafX + x, leafY + y, leafZ + z, BlockId.LEAVES, (byte) 0);
				}
			}
		}
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
		if (id == null) {
			id = BlockId.AIR;
		}
		return id;
	}
}
