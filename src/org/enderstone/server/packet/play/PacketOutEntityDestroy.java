package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketOutEntityDestroy extends Packet {

	private byte length;
	private Integer[] ids;

	public PacketOutEntityDestroy(Integer[] ids) {

		if (ids.length > 127) {
			throw new IllegalArgumentException("You can't despawn more than 127 entities with only one packet.");
		}

		this.length = (byte) ids.length;
		this.ids = ids;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeByte(length);
		for (int i = 0; i < length; i++) {
			buf.writeInt(this.ids[i]);
		}
	}

	@Override
	public int getSize() throws Exception {
		return 1 + (getIntSize() * ids.length) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x13;
	}
}
