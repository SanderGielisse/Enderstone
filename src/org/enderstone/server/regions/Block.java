package org.enderstone.server.regions;

/**
 *
 * @author ferrybig
 */
public interface Block {

	public int getX();

	public int getY();

	public int getZ();

	public EnderWorld getWorld();

	public EnderChunk getChunk();

	public BlockId getBlock();

	public byte getData();

	public void setBlock(BlockId id, byte data);

	public Block getReadOnlyCopy();
	
	public boolean isEditable();
}
