package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutEntityDestroy extends Packet {

	private int length;
	private Integer[] ids;

	public PacketOutEntityDestroy(Integer[] ids) {
		this.length = ids.length;
		this.ids = ids;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeVarInt(length, buf);
		for (int i = 0; i < length; i++) {
			writeVarInt(this.ids[i],buf);
		}
	}

	@Override
	public int getSize() throws IOException {
		int size = 0;
		size += getVarIntSize(length);
		for (int i = 0; i < length; i++) {
			size += getVarIntSize(ids[i]);
		}
		return size + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x13;
	}
}
