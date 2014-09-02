package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.zip.Deflater;
import org.enderstone.server.Location;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketOutChunkData extends Packet {

	private int x;
	private int z;
	private boolean groundUpContinuous;
	private short primaryBitMap;
	private int size;
	private byte[] data;

	public PacketOutChunkData(int x, int z, boolean groundUpContinuous, short primaryBitMap, int size, byte[] data) {
		this.x = x;
		this.z = z;
		this.groundUpContinuous = groundUpContinuous;
		this.primaryBitMap = primaryBitMap;
		this.size = size;
		this.data = data;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(x);
		buf.writeInt(z);
		buf.writeBoolean(groundUpContinuous);
		buf.writeShort(primaryBitMap);
		writeVarInt(size, buf);
		buf.writeBytes(data, 0, size);
	}

	@Override
	public int getSize() throws IOException {
		return (2 * getIntSize()) + 1 + getShortSize() + getVarIntSize(size) + data.length + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x21;
	}

	private final static byte[] emptyChunk;

	static {
		emptyChunk = new byte[256];
	}

	public static PacketOutChunkData clearChunk(int x, int z) {
		return new PacketOutChunkData(x, z, true, (short) 0, emptyChunk.length, emptyChunk);
	}

	@Override
	public String toString() {
		return "PacketOutChunkData{" + "x=" + x + ", z=" + z + ", size=" + size + '}';
	}

}
