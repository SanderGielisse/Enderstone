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
package org.enderstone.server.regions;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.api.Block;
import org.enderstone.server.api.Chunk;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.World;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.packet.play.PacketOutBlockChange;
import org.enderstone.server.regions.io.NBTStorable;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

/**
 *
 * @author Fernando
 */
public class EnderChunk implements Chunk, NBTStorable {

	protected final static int CHUNK_SECTION_SIZE = 16;
	protected final static int MAX_CHUNK_SECTIONS = 16;
	private static final Reference<EnderChunkMap> NULL_REFERENCE = new WeakReference<EnderChunkMap>(null);
	private final int x;
	private final int z;
	private final short[][] blockID;
	private final byte[][] data;
	private final byte[] biome;
	public boolean hasPopulated = false;
	private boolean isValid = true;
	private EnderWorld world;
    private final List<BlockData> blockData;
    public final AtomicReference<ChunkState> chunkState = new AtomicReference<>(ChunkState.LOADED);
    private int unloadTimer = 20;

	public EnderChunk(EnderWorld world, int x, int z, short[][] blockID, byte[][] data, byte[] biome, List<BlockData> blockData) {
		this.world = world;
		if (blockID.length != MAX_CHUNK_SECTIONS) {
			throw new IllegalArgumentException("blockID.length != 16");
		}
		for (int i = 0; i < MAX_CHUNK_SECTIONS; i++) {
			if (blockID[i] == null) continue;
			if (blockID[i].length != MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS)
				throw new IllegalArgumentException("blockID[i].length != MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS");
		}
		if (data.length != MAX_CHUNK_SECTIONS) {
			throw new IllegalArgumentException("data.length != 16");
		}
		for (int i = 0; i < MAX_CHUNK_SECTIONS; i++) {
			if (data[i] == null) continue;
			if (data[i].length != MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS)
				throw new IllegalArgumentException("data[i].length != MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS * MAX_CHUNK_SECTIONS");
		}
		this.z = z;
		this.blockID = blockID;
		this.data = data;
		this.biome = biome;
        this.blockData = blockData;
		this.x = x;
	}
    
    public EnderChunk(EnderWorld world, int x, int z) {
        this(world, x, z, new short[MAX_CHUNK_SECTIONS][], new byte[MAX_CHUNK_SECTIONS][], new byte[16*16], new ArrayList<BlockData>());
    }

    @Override
	public int getZ() {
		return z;
	}

    @Override
	public int getX() {
		return x;
	}

	@Override
	public int hashCode() {
		return this.x << 16 ^ this.z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EnderChunk other = (EnderChunk) obj;
		if (this.z != other.z) {
			return false;
		}
		return this.x == other.x;
	}
    
    public void resetChunkUnloadTimer(int newAmount) {
        if(this.unloadTimer < newAmount)
            this.unloadTimer = newAmount;
    }

	/**
	 * MUST BE CALLED FROM MAIN THREAD
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param material
	 * @param data
	 */
	public void setBlock(int x, int y, int z, BlockId material, byte data) {
        if (this.chunkState.get() == ChunkState.GONE) 
            throw new IllegalStateException("Chunk unloaded");
		// if the Block section the block is in hasn't been used yet, allocate it
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			throw new ArrayIndexOutOfBoundsException("x must be: 0 <= x < 16 (" + x + ") &&" + " y must be: 0 <= y < 256 (" + y + ") &&" + " z must be: 0 <= z < 16 (" + z + ")");
		}
		if (data < 0 || data > 15) {
			throw new IllegalArgumentException("data must be: 0 <= data < 16 (" + data + ")");
		}
		if (material == null)
			material = BlockId.AIR;
		if (blockID[y >> 4] == null) {
			blockID[y >> 4] = new short[16 * 16 * 16];
			this.data[y >> 4] = new byte[16 * 16 * 16];
		}
		blockID[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = material.getId();
		this.data[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = data;
		
		// do this if the block might be able to fall or move by physics
		if (Main.getInstance().doPhysics) {
			world.doTileBlock(x + (16 * this.x), y, z + (16 * this.z));
			world.doTileBlock(x - 1 + (16 * this.x), y, z + (16 * this.z));
			world.doTileBlock(x + 1 + (16 * this.x), y, z + (16 * this.z));
			world.doTileBlock(x + (16 * this.x), y, z + 1 + (16 * this.z));
			world.doTileBlock(x + (16 * this.x), y, z - 1 + (16 * this.z));
			if (y < 255) {
				world.doTileBlock(x + (16 * this.x), y + 1, z + (16 * this.z));
			}
			if (y > 0) {
				world.doTileBlock(x + (16 * this.x), y - 1, z + (16 * this.z));
			}
		}
		
		for (EnderPlayer player : Main.getInstance().onlinePlayers) {
			if (player.getWorld().worldName.equals(world.worldName)) {
				if (player.getLoadedChunks().contains(this)) {
					player.getNetworkManager().sendPacket(new PacketOutBlockChange(new Location(player.getWorld(), (this.getX() * 16) + x, y, (this.getZ() * 16) + z, (float) 0, (float) 0), material.getId(), data));
				}
			}
		}
        this.chunkState.set(ChunkState.LOADED_SAVE);
		compressed = NULL_REFERENCE;
	}

	public BlockId getBlock(int x, int y, int z) {
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			throw new ArrayIndexOutOfBoundsException("x must be: -1 < x < 16 (" + x + ") &&" + " y must be: -1 < y < 256 (" + y + ") &&" + " z must be: -1 < z < 16 (" + z + ")");
		}
		// if the Block section the block is in hasn't been used yet, allocate
		// it
		if (blockID[y >> 4] == null) {
			return BlockId.AIR; // block is air as it hasnt been allocated
		}
		return BlockId.byId(blockID[y >> 4][((y & 0xF) << 8) | (z << 4) | x]);

	}

	public byte getData(int x, int y, int z) {
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			throw new ArrayIndexOutOfBoundsException("x must be: -1 < x < 16 (" + x + ") &&" + " y must be: -1 < y < 256 (" + y + ") &&" + " z must be: -1 < z < 16 (" + z + ")");
		}
		// if the Block section the block is in hasn't been used yet, allocate
		// it
		if (data[y >> 4] == null) {
			return 0; // block is air as it hasnt been allocated
		}
		return data[y >> 4][((y & 0xF) << 8) | (z << 4) | x];

	}

	public Reference<EnderChunkMap> compressed = NULL_REFERENCE;

	/**
	 * Gets the format used by the chunk packet
	 *
	 * @return
	 */
	public EnderChunkMap getCompressedChunk() // TODO add block / skyligth
	{
		EnderChunkMap map;
		if ((map = compressed.get()) != null) {
			return map;
		}
		map = build();
		compressed = new WeakReference<>(map);
		return map;
	}

	public EnderChunkMap build() {
		int totalChunkSize = 65535;
		int currentIndex = 0;
		EnderChunkMap chunkmap = new EnderChunkMap();
		byte[] totalArray = new byte[196864];
		int blockLength;
		/**
		 * Calculate bitmasks & empty sections
		 */
		{
			for (blockLength = 0; blockLength < blockID.length; blockLength++) {
				if (blockID[blockLength] != null && (totalChunkSize & 1 << blockLength) != 0) {
					chunkmap.primaryBitmap |= 1 << blockLength;
				}
			}
		}
		/**
		 * Write first byte of block id's
		 */
		{
			for (blockLength = 0; blockLength < blockID.length; blockLength++) {

				if ((blockID[blockLength] != null) && ((totalChunkSize & 1 << blockLength) != 0)) {
					int lastIndex = currentIndex;
					short[] idArray = this.blockID[blockLength];
					for (int idSize = 0; idSize < idArray.length; idSize++) {
						int id = idArray[idSize] & 0xFF;
						int dataId = this.data[blockLength][idSize];
						char val = (char) (id << 4 | dataId);
						totalArray[(currentIndex++)] = ((byte) (val & 0xFF));
						totalArray[(currentIndex++)] = ((byte) (val >> '\b' & 0xFF));
					}
					assert lastIndex + blockID[blockLength].length * 2 == currentIndex;
				}
			}
		}
		/**
		 * Block Light - uses data because its the same size as the lighting array we don't have
		 */
		{
			for (blockLength = 0; blockLength < data.length; ++blockLength) {
				if (data[blockLength] != null && (totalChunkSize & 1 << blockLength) != 0) {
					int lastIndex = currentIndex;
					byte[] nibblearray = data[blockLength];
					byte halfData = 0;
					boolean hd = false;

					for (int counter = 0; counter < nibblearray.length; counter++) {
						byte blockLigth = 15;
						if (hd) {
							halfData = (byte) ((blockLigth << 4) | halfData);
							totalArray[currentIndex++] = halfData;
						} else {
							halfData = blockLigth;
						}
						hd = !hd;
					}
					assert lastIndex + data[blockLength].length / 2 == currentIndex;
					// j += nibblearray.length / 2 ;
				}
			}
		}
		/**
		 * Skylight - uses BlockID array because its the same size as the sky array we don't have
		 */
		{
			for (blockLength = 0; blockLength < blockID.length; ++blockLength) {
				if (blockID[blockLength] != null && (totalChunkSize & 1 << blockLength) != 0) {
					int lastIndex = currentIndex;
					short[] nibblearray = blockID[blockLength];
					byte halfData = 0;
					boolean hd = false;

					for (int counter = 0; counter < nibblearray.length; counter++) {
						byte skyLigth = (byte) (15);
						if (hd) {
							halfData = (byte) ((skyLigth << 4) | halfData);
							totalArray[currentIndex++] = halfData;
						} else {
							halfData = skyLigth;
						}
						hd = !hd;
					}
					assert lastIndex + data[blockLength].length / 2 == currentIndex;
					// j += nibblearray.length / 2 ;
				}
			}
		}
		/**
		 * Biomes
		 */
		{
			byte[] newArray = biome;
			System.arraycopy(newArray, 0, totalArray, currentIndex, newArray.length);
			currentIndex += newArray.length;
		}
		/**
		 * Copy
		 */
		{
			chunkmap.chunkData = new byte[currentIndex];
			System.arraycopy(totalArray, 0, chunkmap.chunkData, 0, currentIndex);
		}
		return chunkmap;
	}

    @Override
	public int getHighestBlockAt(int x, int z) {
		for (int i = 255; i > 0; i--) {
			BlockId bl = this.getBlock(x, i, z);
			if (!bl.equals(BlockId.AIR)) {
				return i;
			}
		}
		return (short) 0;
	}

	public boolean isValid() {
		return this.isValid;
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		return new EnderBlock(x + (16 * this.x), y, z + (16 * this.z), this.world); //TODO test this
	}

	@Override
	public Block getBlockAt(Location location) {
		return this.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	@Override
	public World getWorld() {
		return this.world;
	}

    @Override
    public CompoundTag saveToNBT() {
        //TODO throw new UnsupportedOperationException("Not supported yet.");
        return new CompoundTag("level", Collections.<String, Tag>emptyMap());
    }

    @Override
    public void loadFromNBT(CompoundTag tag) {
        // TODO throw new UnsupportedOperationException("Not supported yet.");
    }

    public void serverTick() {
        // do tile blocks like furnaces and hoppers here
    }
    
    public boolean tickUnload() {
        if (this.unloadTimer != Integer.MAX_VALUE && this.unloadTimer != Integer.MIN_VALUE) {
            this.unloadTimer--;
        }
        return this.unloadTimer < 0;
    }

    public int getUnloadTimer() {
        return this.unloadTimer;
    }

    @Override
    public String toString() {
        return "EnderChunk{" + "x=" + x + ", z=" + z + ", isValid=" + isValid + ", chunkState=" + chunkState + ", unloadTimer=" + unloadTimer + '}';
    }
    
    

    public enum ChunkState {

        LOADED, LOADED_SAVE,
        UNLOADED, UNLOADED_SAVE,
        SAVING, GONE
    }

}
