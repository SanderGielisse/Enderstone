package org.enderstone.server.regions.generators;

import java.util.Random;

import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.generators.util.SimplexOctaveGenerator;

public class TimTest extends ChunkGenerator {

	@Override
	public BlockId[][] generateExtBlockSections(EnderWorld world,
			Random random, int i, int o, BiomeGrid biomes) {
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

				double maxHeight = land.noise(realX, realZ, frequency,
						amplitude) * multitude + sea_level;
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
}
