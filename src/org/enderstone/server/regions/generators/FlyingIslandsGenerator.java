package org.enderstone.server.regions.generators;

import java.util.Random;

import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.generators.util.SimplexOctaveGenerator;

/**
 *
 * @author Fernando
 */
public class FlyingIslandsGenerator extends ChunkGenerator {

	/**
	 *
	 * @param x
	 *            X co-ordinate of the block to be set in the array
	 * @param y
	 *            Y co-ordinate of the block to be set in the array
	 * @param z
	 *            Z co-ordinate of the block to be set in the array
	 * @param chunk
	 *            An array containing the Block id's of all the blocks in the
	 *            chunk. The first offset is the block section number. There are
	 *            16 block sections, stacked vertically, each of which 16 by 16
	 *            by 16 blocks.
	 * @param material
	 *            The material to set the block to.
	 */
	private void setBlock(int x, int y, int z, BlockId[][] chunk, BlockId material) {
		// if the Block section the block is in hasn't been used yet, allocate
		// it
		if (chunk[y >> 4] == null) {
			chunk[y >> 4] = new BlockId[16 * 16 * 16];
		}
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			return;
		}
		try {
			chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = material;
		} catch (Exception e) {
			// do nothing
		}
	}

	private BlockId getBlock(int x, int y, int z, BlockId[][] chunk) {
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			return BlockId.AIR;
		}
		// if the Block section the block is in hasn't been used yet, allocate
		// it
		if (chunk[y >> 4] == null) {
			return BlockId.AIR; // block is air as it hasnt been allocated
		}
		return chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x];

	}

	@Override
	public BlockId[][] generateExtBlockSections(EnderWorld world, Random random, int ChunkX, int ChunkZ, ChunkGenerator.BiomeGrid biomes) {
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
}