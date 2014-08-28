package org.enderstone.server.packet.play;

import java.util.zip.Deflater;
import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketOutChunkData extends Packet {

	private int x;
	private int z;
	private boolean groundUpContinuous;
	private short primaryBitMap;
	private short addBitMap;
	private int size;
	private byte[] data;

	public PacketOutChunkData(int x, int z, boolean groundUpContinuous, short primaryBitMap, short addBitMap, int size, byte[] data) {
		this.x = x;
		this.z = z;
		this.groundUpContinuous = groundUpContinuous;
		this.primaryBitMap = primaryBitMap;
		this.addBitMap = addBitMap;
		this.size = size;
		this.data = data;
	}

	public PacketOutChunkData() {
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeInt(x);
		buf.writeInt(z);
		buf.writeBoolean(groundUpContinuous);
		buf.writeShort(primaryBitMap);
		buf.writeShort(addBitMap);
		buf.writeInt(size);
		buf.writeBytes(data, 0, size);
	}

	@Override
	public int getSize() throws Exception {
		return (3 * getIntSize()) + 1 + (2 * getShortSize()) + data.length + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x21;
	}

	private final static byte[] emptyChunk;

	static {
		byte[] buildBuffer = new byte[256];
		Deflater deflator = new Deflater(9);
		deflator.setInput(buildBuffer);
		deflator.finish();
		int size = deflator.deflate(buildBuffer);
		emptyChunk = new byte[size];
		System.arraycopy(buildBuffer, 0, emptyChunk, 0, size);
	}

	public static PacketOutChunkData clearChunk(int x, int z) {
		return new PacketOutChunkData(x, z, true, (short) 0, (short) 0, emptyChunk.length, emptyChunk);
	}
}
