/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.bigteddy98.mcserver.regions;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class EnderChunk {

	protected final static int CHUNK_SECTION_SIZE = 16;
	protected final static int MAX_CHUNK_SECTIONS = 16;
	private final int z;
	private final short[][] blockID;
	private final byte[][] data;
	private final byte[] biome;
	private final List<BlockData> blockData;
	private final List<BlockData> activeBlockData = new LinkedList<>();
	private final int x;

	public EnderChunk(int x, int z, short[][] blockID, byte[][] data, byte[] biome, List<BlockData> blockData) {
		this.z = z;
		this.blockID = blockID;
		this.data = data;
		this.biome = biome;
		this.blockData = blockData;
		this.x = x;
	}

	public int getZ() {
		return z;
	}

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

	public WeakReference<EnderChunkMap> compressed = new WeakReference<>(null);

	/**
	 * Gets the format used by the chunk packet
	 *
	 * @return
	 */
	public EnderChunkMap getCompressedChunk() // todo add block / skyligth
	{
		EnderChunkMap map = build(this, false, 65535);
		compressed = new WeakReference<EnderChunkMap>(map);
		return map;
	}

	@SuppressWarnings("unused")
	public static EnderChunkMap build(EnderChunk chunk, boolean flag, int i) {
		int j = 0;

		int k = 0;
		EnderChunkMap chunkmap = new EnderChunkMap();
		byte[] abyte = new byte[196864];
		int l;
		/**
		 * Calculate bitmasks & empty sections
		 */
		{
			for (l = 0; l < chunk.blockID.length; ++l) {
				if (chunk.blockID[l] != null && (!flag) && (i & 1 << l) != 0) {
					chunkmap.primaryBitmap |= 1 << l;
					for (short s : chunk.blockID[l]) {
						if (s > 255) {
							// chunkmap.extendedBitmap |= 1 << l; // No support
							// for extended block ids
							++k;
							break;
						}
					}
				}
			}
		}
		/**
		 * Write first byte of block id's
		 */
		{
			for (l = 0; l < chunk.blockID.length; ++l) {
				if (chunk.blockID[l] != null && (!flag) && (i & 1 << l) != 0) {
					short[] abyte1 = chunk.blockID[l];
					for (int t = 0; t < abyte1.length; t++) {
						abyte[t + j] = (byte) abyte1[t];
					}
					j += abyte1.length;
				}
			}
		}
		/**
		 * Write data of blocks
		 */
		{
			for (l = 0; l < chunk.data.length; ++l) {
				if (chunk.data[l] != null && (!flag) && (i & 1 << l) != 0) {
					byte[] nibblearray = chunk.data[l];
					byte halfData = 0;
					boolean hd = false;

					for (byte block : nibblearray) {
						if (hd) {
							halfData = (byte) (halfData << 4);

							abyte[j++] = (byte) (halfData | block);
						} else {
							halfData = block;
						}
						hd = !hd;

					}
					// j += nibblearray.length / 2 ;
				}
			}
		}
		/**
		 * Block Ligth - uses data because its the same size as the ligthing
		 * array we don't have
		 */
		{
			for (l = 0; l < chunk.data.length; ++l) {
				if (chunk.data[l] != null && (!flag) && (i & 1 << l) != 0) {
					byte[] nibblearray = chunk.data[l];
					byte halfData = 0;
					boolean hd = false;

					for (byte block : nibblearray) {
						if (hd) {
							halfData = (byte) (halfData << 4);

							abyte[j++] = (byte) (halfData | 0);
						} else {
							halfData = 0;
						}
						hd = !hd;
					}
					// j += nibblearray.length / 2 ;
				}
			}
		}
		/**
		 * Sky light - uses blockid because its the same size as the sky array
		 * we don't have
		 */
		{
			for (l = 0; l < chunk.blockID.length; ++l) {
				if (chunk.blockID[l] != null && (!flag) && (i & 1 << l) != 0) {
					short[] nibblearray = chunk.blockID[l];
					byte halfData = 0;
					boolean hd = false;

					for (short block : nibblearray) {
						if (hd) {
							halfData = (byte) (halfData << 4);

							abyte[j++] = (byte) (halfData | (block == 0 ? 15 : 0));
						} else {
							halfData = (byte) (block == 0 ? 15 : 0);
						}
						hd = !hd;
					}
					// j += nibblearray.length / 2 ;
				}
			}
		}
		/**
		 * Extended blocks, skip this
		 */
		{

		}
		/**
		 * Biomes
		 */
		{
			if (flag) {
				byte[] abyte2 = chunk.biome;

				System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
				j += abyte2.length;
			}
		}
		/**
		 * Copy
		 */
		chunkmap.chunkData = new byte[j];
		System.arraycopy(abyte, 0, chunkmap.chunkData, 0, j);

		return chunkmap;
	}
}
