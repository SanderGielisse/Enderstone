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

import java.util.Random;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;

/**
 *
 * @author Fernando
 */
public interface MultiChunkBlockPopulator {

	public void populate(EnderWorld sourceWorld, Random random, ChunkGrid chunkGrid);

	public class ChunkGrid {

		private final EnderChunk[] chunks;

		public ChunkGrid(EnderChunk[] chunks) {
			if (chunks.length != 9) throw new IllegalArgumentException("chunks.length != 9 (" + chunks.length + ")");
			this.chunks = chunks;
		}
		
		public EnderChunk getPrimairyChunk()
		{
			return chunks[1+1*3];
		}

		public void setBlock(int x, int y, int z, BlockId id, byte data) {
			getChunk(x >> 4, z >> 4).setBlock(x & 0xF, y & 0xFF, z & 0xF, id, data);
		}

		public BlockId getBlock(int x, int y, int z) {
			return getChunk(x >> 4, z >> 4).getBlock(x & 0xF, y & 0xFF, z & 0xF);
		}

		public byte getData(int x, int y, int z) {
			return getChunk(x >> 4, z >> 4).getData(x & 0xF, y & 0xFF, z & 0xF);
		}

		private EnderChunk getChunk(int x, int z) {
			if (x < -1 || x > 1) throw new IllegalArgumentException("A MultiChunkBlockPopulator is called for chunk blocks of size -16 to 32, you passed a chunk x of: " + x);
			if (z < -1 || z > 1) throw new IllegalArgumentException("A MultiChunkBlockPopulator is called for chunk blocks of size -16 to 32, you passed a chunk z of: " + z);
			x++;
			z++;
			return chunks[x + z * 3];
		}

	}
}
