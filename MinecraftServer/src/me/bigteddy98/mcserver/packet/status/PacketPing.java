package me.bigteddy98.mcserver.packet.status;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketPing extends Packet {

	private long time;

	public PacketPing() {
	}

	public PacketPing(long time) {
		this.time = time;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		time = buf.readLong();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeLong(time);
	}

	@Override
	public int getSize() throws Exception {
		return getLongSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}
}
