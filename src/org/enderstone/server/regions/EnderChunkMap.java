package org.enderstone.server.regions;

import java.util.zip.Deflater;
import org.enderstone.server.packet.play.PacketOutChunkData;

/**
 *
 * @author Fernando
 */
public class EnderChunkMap {

	public byte[] chunkData;
	public int primaryBitmap;
	public int extendedBitmap;
	private byte[] compressed;

	public byte[] getCompressed() {

		if (compressed != null) {
			return compressed;
		}
		Deflater deflater = new Deflater(-1);
		try {
			deflater.setInput(chunkData, 0, chunkData.length);
			deflater.finish();
			byte[] bytes = new byte[chunkData.length];

			int size = deflater.deflate(bytes);
			this.compressed = new byte[size];
			System.arraycopy(bytes, 0, this.compressed, 0, size);
		} finally {
			deflater.end();
		}
		return compressed;
	}

	public PacketOutChunkData toPacket(int x, int z) {
		byte[] com = this.getCompressed();
		//TODO
		return new PacketOutChunkData(x, z, true, (short) (primaryBitmap & '\uffff'), com.length, com);
	}
}
