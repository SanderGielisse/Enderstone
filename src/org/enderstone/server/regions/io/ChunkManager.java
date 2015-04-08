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

package org.enderstone.server.regions.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.regions.BlockData;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;
import static org.enderstone.server.regions.EnderWorld.AMOUNT_OF_CHUNKSECTIONS;
import org.enderstone.server.regions.RegionSet;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

public class ChunkManager {
	
    /**
     * Stored the chunk generator fo this world
     */
	private final ChunkGenerator generator;
	
    /**
     * Region files of this world
     */
	private final File regionDirectory;
    
    /**
     * Cache for open regionFiles
     */
    private final Map<Long,RegionFile> regionFileCache = new HashMap<>();
	
    /**
     * Thrown away chunks that are unloaded, these objects are kept inside this
     * list so we can reuse the memory without the garbage collector
     * causing lagg
     */
	private final List<EnderChunk> unusedChunks = new ArrayList<>();
	
    private final RegionSet loadedChunks;
    private final EnderWorld world;
    
    public ChunkManager(ChunkGenerator generator, File regionDirectory, EnderWorld world) {
        this.generator = generator;
        this.regionDirectory = regionDirectory;
        this.loadedChunks = new RegionSet();
        this.world = world;
    }
	
	public EnderChunk getChunk(int x, int z) {
        EnderChunk c;
        if((c = this.loadedChunks.get(x, z)) == null) {
            if((c = this.loadChunk(x, z)) == null) {
                if((c = this.createChunk(x, z)) == null) {
                    throw new RuntimeException("Unable to create a chunk?!?! This won't happen as createChunk always returns a valid chunk");
                } 
            }
            this.loadedChunks.add(c);
        }
        markChunkUsed(x, z);
		return c;
	}
	
	public void saveChunks() {
		
	}
    
    public void markChunkUsed(int chunkX, int chunkZ) {
        
    }
    
    private void saveChunk(EnderChunk chunk) {
        int x = chunk.getX(), z = chunk.getZ();
        long key = ((long) calculateRegionPos(x) << 32) ^ calculateRegionPos(z);
        synchronized (regionFileCache) {
            RegionFile region = regionFileCache.get(Long.valueOf(key));
            if (region == null) {
                region = new RegionFile(new File(regionDirectory, "r."+calculateRegionPos(x)+"."+calculateRegionPos(z)+".mca"));
                regionFileCache.put(key, region);
            }
            try(DataOutputStream out = region.getChunkDataOutputStream(calculateChunkPos(x), calculateChunkPos(z))) {
                
            } catch (IOException ex) {
                EnderLogger.warn("Error while saving chunk");
                EnderLogger.exception(ex);
            }
        }
    }
    
    private EnderChunk loadChunk(int x, int z) {
        long key = ((long) calculateRegionPos(x) << 32) ^ calculateRegionPos(z);
        EnderChunk c;
        DataInputStream in;
        synchronized (regionFileCache) {
            RegionFile region = regionFileCache.get(Long.valueOf(key));
            if (region == null) {
                region = new RegionFile(new File(regionDirectory, "r."+calculateRegionPos(x)+"."+calculateRegionPos(z)+".mca"));
                regionFileCache.put(key, region);
            }
            in = region.getChunkDataInputStream(calculateChunkPos(x), calculateChunkPos(z));
        }
        try (NBTInputStream indata = new NBTInputStream(in)) {
            CompoundTag tag = (CompoundTag) indata.readTag();
            // read tag to chunk, http://minecraft.gamepedia.com/Chunk_format
            c = null;
        } catch (IOException ex) {
            EnderLogger.warn("Error while loading chunk");
            EnderLogger.exception(ex);
            return null;
        }
        return c;
    }

    private EnderChunk createChunk(int x, int z) {
        EnderChunk r;
        BlockId[][] blocks = generator.generateExtBlockSections(world, new Random((((long)x) << 32) ^ z), x, z);
		if (blocks == null) {
			blocks = new BlockId[AMOUNT_OF_CHUNKSECTIONS][];
		}
		if (blocks.length != AMOUNT_OF_CHUNKSECTIONS) {
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
		r = new EnderChunk(world, x, z, id, data, new byte[16 * 16], new ArrayList<BlockData>());
		return r;
    }
    
    private static int calculateChunkPos(int rawChunkLocation) {
		rawChunkLocation %= 32;
		if (rawChunkLocation < 0) {
			rawChunkLocation += 32;
		}
		return rawChunkLocation;
	}

	protected static int calculateRegionPos(int raw) {
		raw = raw >> 5;
		return raw;
	}
}
