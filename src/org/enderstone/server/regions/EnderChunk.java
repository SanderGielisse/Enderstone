package org.enderstone.server.regions;

import static java.lang.System.out;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.play.PacketOutBlockChange;

/**
 *
 * @author Fernando
 */
public class EnderChunk {

	protected final static int CHUNK_SECTION_SIZE = 16;
	protected final static int MAX_CHUNK_SECTIONS = 16;
	private static final Reference<EnderChunkMap> NULL_REFERENCE = new WeakReference(null);
	private final int z;
	private final short[][] blockID;
	private final byte[][] data;
	private final byte[] biome;
	private final List<BlockData> blockData;
	private final List<BlockData> activeBlockData = new LinkedList<>();
	private final int x;
	public boolean hasPopulated = false;
	private boolean isValid = true;

	public EnderChunk(int x, int z, short[][] blockID, byte[][] data, byte[] biome, List<BlockData> blockData) {
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
		// if the Block section the block is in hasn't been used yet, allocate
		// it
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

		for (EnderPlayer player : Main.getInstance().onlinePlayers) {
			if (Main.getInstance().mainWorld.players.containsKey(player)) {
				if (Main.getInstance().mainWorld.players.get(player).contains(this)) {
					try {
						player.getNetworkManager().sendPacket(new PacketOutBlockChange(new Location("", (this.getX() * 16) + x, y, (this.getZ() * 16) + z, (float) 0, (float) 0), material.getId(), data));
					} catch (Exception e) {
						EnderLogger.exception(e);
					}
				}
			}
		}
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
		map = build(false, 65535);
		compressed = new WeakReference<>(map);
		return map;
	}

	public EnderChunkMap build(boolean flag, int i) {
		int j = 0;

		EnderChunkMap chunkmap = new EnderChunkMap();
		byte[] abyte = new byte[196864];
		int l;
		/**
		 * Calculate bitmasks & empty sections
		 */
		{
			for (l = 0; l < blockID.length; ++l) {
				if (blockID[l] != null && (!flag) && (i & 1 << l) != 0) {
					chunkmap.primaryBitmap |= 1 << l;
				}
			}
		}
		/**
		 * Write first byte of block id's
		 */
		{
//			for (l = 0; l < chunk.blockID.length; ++l) {
//				if (chunk.blockID[l] != null && (!flag) && (i & 1 << l) != 0) {
//					short[] abyte1 = chunk.blockID[l];
//					for (int t = 0; t < abyte1.length; t++) {
//						int full = abyte1[t] << 4 | chunk.data[l][t];
//						abyte[(t << 1) + j] = (byte) (abyte1[t] >> 8);
//						abyte[(t << 1) + 1 + j] = (byte) (abyte1[t] & 0b1111_1111);
//					}
//					j += abyte1.length * 2;
//				}
//			}
			for (l = 0; l < blockID.length; l++) {

				if ((blockID[l] != null) && ((!flag) || (blockID[l] != null)) && ((i & 1 << l) != 0)) {
					int lastIndex = j;
					short[] idArray = this.blockID[l];
					for (int ind = 0; ind < idArray.length; ind++) {
						int id = idArray[ind] & 0xFF;
						int dataId = this.data[l][ind];
						char val = (char) (id << 4 | dataId);
						abyte[(j++)] = ((byte) (val & 0xFF));
						abyte[(j++)] = ((byte) (val >> '\b' & 0xFF));
					}
					assert lastIndex + blockID[l].length * 2 == j;
				}

			}
		}
		/**
		 * Block Ligth - uses data because its the same size as the ligthing array we don't have
		 */
		{
			for (l = 0; l < data.length; ++l) {

				if (data[l] != null && (!flag) && (i & 1 << l) != 0) {
					int lastIndex = j;
					byte[] nibblearray = data[l];
					byte halfData = 0;
					boolean hd = false;

					for (byte block : nibblearray) {
						byte blockLigth = 13;
						if (hd) {
							halfData = (byte) ((blockLigth << 4) | halfData);
							abyte[j++] = halfData;
						} else {
							halfData = blockLigth;
						}
						hd = !hd;
					}
					assert lastIndex + data[l].length / 2 == j;
					// j += nibblearray.length / 2 ;
				}
			}
		}
		/**
		 * Sky light - uses blockid because its the same size as the sky array we don't have
		 */
		{
			for (l = 0; l < blockID.length; ++l) {
				if (blockID[l] != null && (!flag) && (i & 1 << l) != 0) {
					int lastIndex = j;
					short[] nibblearray = blockID[l];
					byte halfData = 0;
					boolean hd = false;

					for (short block : nibblearray) {
						byte skyLigth = 13;
						if (hd) {
							halfData = (byte) ((skyLigth << 4) | halfData);
							abyte[j++] = halfData;
						} else {
							halfData = skyLigth;
						}
						hd = !hd;
					}
					assert lastIndex + data[l].length / 2 == j;
					// j += nibblearray.length / 2 ;
				}
			}
		}
		/**
		 * Biomes
		 */
		{
			byte[] abyte2 = biome;

			System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
			j += abyte2.length;
		}
		/**
		 * Copy
		 */

		chunkmap.chunkData = new byte[j];
		System.arraycopy(abyte, 0, chunkmap.chunkData, 0, j);

		return chunkmap;
	}

	public int getHighestBlock(int x, int z) {
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

	public final AtomicReference<ChunkState> chunkState = new AtomicReference<>(ChunkState.LOADED);

	public enum ChunkState {

		LOADED, LOADED_SAVE,
		UNLOADED, UNLOADED_SAVE,
		SAVING, GONE
	}
}
