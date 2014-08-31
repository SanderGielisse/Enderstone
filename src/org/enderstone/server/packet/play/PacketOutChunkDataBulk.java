package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderChunkMap;

/**
 *
 * @author ferrybig
 */
public class PacketOutChunkDataBulk extends Packet {

	private final int[] chunkX;
	private final int[] chunkZ;
	private final int[] primaryBitmask;
	private final int[] addBitmask;
	private byte[] buffer;
	private int size;
	private final boolean skyligth;
	private byte[] buildBuffer = new byte[0];
	static final ThreadLocal<Deflater> localDeflater = new ThreadLocal() {
		@Override
		protected Deflater initialValue() {
			return new Deflater(6);
		}
	};

	public PacketOutChunkDataBulk(List<EnderChunk> list) {
		int i = list.size();
		System.out.println(i);
		this.chunkX = new int[i];
		this.chunkZ = new int[i];
		this.primaryBitmask = new int[i];
		this.addBitmask = new int[i];
		this.skyligth = true; // contains skyligth
		int j = 0;
		for (int k = 0; k < i; k++) {
			EnderChunk chunk = (EnderChunk) list.get(k);
			EnderChunkMap chunkmap = EnderChunk.build(chunk, false, 65535);
			if (this.buildBuffer.length < j + chunkmap.chunkData.length) {
				byte[] abyte = new byte[j + chunkmap.chunkData.length];

				System.arraycopy(this.buildBuffer, 0, abyte, 0, this.buildBuffer.length);
				this.buildBuffer = abyte;
			}
			System.arraycopy(chunkmap.chunkData, 0, this.buildBuffer, j, chunkmap.chunkData.length);
			j += chunkmap.chunkData.length;
			this.chunkX[k] = chunk.getX();
			this.chunkZ[k] = chunk.getZ();
			this.primaryBitmask[k] = chunkmap.primaryBitmap;
			this.addBitmask[k] = chunkmap.extendedBitmap;
		}
	}

	public void compress() {
		if (this.buffer != null) {
			return;
		}
		Deflater deflater = (Deflater) localDeflater.get();
		deflater.reset();
		deflater.setInput(this.buildBuffer);
		deflater.finish();
		System.out.println("Build: " + this.buildBuffer.length);
		this.buffer = new byte[this.buildBuffer.length + 100];

		this.size = deflater.deflate(this.buffer);
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		compress();
		buf.writeShort(this.chunkX.length);
		buf.writeInt(this.size);
		buf.writeBoolean(this.skyligth);
		buf.writeBytes(this.buffer, 0, this.size);
		System.out.println("Chunk: " + this.size);
		for (int i = 0; i < this.chunkX.length; i++) {
			buf.writeInt(this.chunkX[i]);
			buf.writeInt(this.chunkZ[i]);
			buf.writeShort((short) (this.primaryBitmask[i] & 0xFFFF));
			buf.writeShort((short) (this.addBitmask[i] & 0xFFFF));
		}
	}

	@Override
	public int getSize() throws IOException {
		compress();
		int s = 0;
		s += 2;
		s += 4;
		s += 1;
		s += this.size;
		for (int i = 0; i < this.chunkX.length; i++) {
			s += 4;
			s += 4;
			s += 2;
			s += 2;
		}
		return s + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x26;
	}
}
