package me.bigteddy98.mcserver.regions;

import java.util.zip.Deflater;
import me.bigteddy98.mcserver.packet.play.PacketOutChunkData;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
		return new PacketOutChunkData(x, z, true, (short) (primaryBitmap & '\uffff'), (short) (extendedBitmap & '\uffff'), com.length, com);
	}
}