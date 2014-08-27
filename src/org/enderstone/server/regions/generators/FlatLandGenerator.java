package me.bigteddy98.mcserver.regions.generators;

import java.util.Random;

import me.bigteddy98.mcserver.regions.BlockId;
import me.bigteddy98.mcserver.regions.ChunkGenerator;
import me.bigteddy98.mcserver.regions.EnderWorld;

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
}
