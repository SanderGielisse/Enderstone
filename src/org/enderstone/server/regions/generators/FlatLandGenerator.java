package org.enderstone.server.regions.generators;

import java.util.List;
import java.util.Random;

import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.BlockPopulator;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;

public class FlatLandGenerator extends ChunkGenerator {
	@Override
	public BlockId[][] generateExtBlockSections(EnderWorld world, Random random, int x, int z, ChunkGenerator.BiomeGrid biomes) {
		BlockId[][] r = new BlockId[16][];
		
		r[0] = new BlockId[4096];
		for (int i = 0; i < r[0].length; i++) {
			r[0][i] = BlockId.GRASS;
		}
		return r;
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(EnderWorld world) {
		List<BlockPopulator> p = super.getDefaultPopulators(world);
		p.add(new BlockPopulator() {

			@Override
			public void populate(EnderWorld world, Random random, EnderChunk source) {
				source.setBlock(7, 16, 7, BlockId.COBBLESTONE,(byte) 0);
			}
		});
		return p;
	}
	
	
}
