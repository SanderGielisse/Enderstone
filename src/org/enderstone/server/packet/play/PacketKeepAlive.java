package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketKeepAlive extends Packet {

	private int keepAliveId;

	public PacketKeepAlive() {
	}

	public PacketKeepAlive(int keepAliveId) {
		this.keepAliveId = keepAliveId;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.keepAliveId = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeInt(keepAliveId);
	}

	@Override
	public int getSize() throws Exception {
		return getIntSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}
}
