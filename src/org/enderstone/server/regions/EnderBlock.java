package org.enderstone.server.regions;

import java.util.Objects;

/**
 *
 * @author ferrybig
 */
public class EnderBlock implements Block {

	private final int x, y, z;
	private final EnderWorld world;
	private final EnderChunk chunk;

	public EnderBlock(int x, int y, int z, EnderWorld world) {
		this(x, y, z, world, world.getOrCreateChunk(x >> 4, z >> 4));
	}

	private EnderBlock(int x, int y, int z, EnderWorld world, EnderChunk chunk) {
		if (!(y <= 256 && y >= 0)) {
			throw new ArrayIndexOutOfBoundsException("y must be: 0 <= y < 256 (" + y + ")");
		}
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.chunk = chunk;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public EnderWorld getWorld() {
		return world;
	}

	@Override
	public EnderChunk getChunk() {
		return chunk;
	}

	@Override
	public BlockId getBlock() {
		if (chunk.isValid())
			return chunk.getBlock(x & 0xF, y, z & 0xF);
		throw new IllegalStateException("Chunk unloaded!");

	}

	@Override
	public byte getData() {
		if (chunk.isValid())
			return chunk.getData(x & 0xF, y, z & 0xF);
		throw new IllegalStateException("Chunk unloaded!");
	}

	@Override
	public void setBlock(BlockId id, byte data) {
		chunk.setBlock(x & 0xF, y, z & 0xF, id, data);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + this.x;
		hash = 71 * hash + this.y;
		hash = 71 * hash + this.z;
		hash = 71 * hash + Objects.hashCode(this.world);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final EnderBlock other = (EnderBlock) obj;
		if (this.x != other.x) return false;
		if (this.y != other.y) return false;
		if (this.z != other.z) return false;
		if (!Objects.equals(this.world, other.world)) return false;
		return true;
	}

	@Override
	public Block getReadOnlyCopy() {
		return new Block() {
			BlockId id = EnderBlock.this.getBlock();
			byte data = EnderBlock.this.getData();
			int x = EnderBlock.this.getX();
			int y = EnderBlock.this.getY();
			int z = EnderBlock.this.getZ();
			EnderChunk chunk = EnderBlock.this.getChunk();
			EnderWorld world = EnderBlock.this.getWorld();

			@Override
			public byte getData() {
				return data;
			}

			@Override
			public int getX() {
				return x;
			}

			@Override
			public int getY() {
				return y;
			}

			@Override
			public int getZ() {
				return z;
			}

			@Override
			public EnderChunk getChunk() {
				return chunk;
			}

			@Override
			public EnderWorld getWorld() {
				return world;
			}

			@Override
			public BlockId getBlock() {
				return id;
			}

			@Override
			public Block getReadOnlyCopy() {
				return this;
			}

			@Override
			public int hashCode() {
				int hash = 5;
				hash = 71 * hash + this.x;
				hash = 71 * hash + this.y;
				hash = 71 * hash + this.z;
				hash = 71 * hash + Objects.hashCode(this.world);
				return hash;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == null) return false;
				if (getClass() != obj.getClass()) return false;
				final EnderBlock other = (EnderBlock) obj;
				if (this.x != other.x) return false;
				if (this.y != other.y) return false;
				if (this.z != other.z) return false;
				if (!Objects.equals(this.world, other.world)) return false;
				return true;
			}

			@Override
			public void setBlock(BlockId id, byte data) {
				throw new IllegalStateException("Cannot set the block of a readonly copy");
			}

			@Override
			public boolean isEditable() {
				return false;
			}
		};

	}

	@Override
	public boolean isEditable() {
		return true;
	}
}
