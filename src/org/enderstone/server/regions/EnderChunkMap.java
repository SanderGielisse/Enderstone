package org.enderstone.server.regions;

import org.enderstone.server.packet.play.PacketOutChunkData;

/**
 *
 * @author Fernando
 */
public class EnderChunkMap {

	public byte[] chunkData;
	public int primaryBitmap;
	public int extendedBitmap;

	public PacketOutChunkData toPacket(int x, int z) {
		return new PacketOutChunkData(x, z, true, (short) (primaryBitmap & '\uffff'), chunkData.length, chunkData);
	}
}
