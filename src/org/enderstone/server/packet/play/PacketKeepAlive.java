package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketKeepAlive extends Packet {

	private int keepAliveId;

	public PacketKeepAlive() {
	}

	public PacketKeepAlive(int keepAliveId) {
		this.keepAliveId = keepAliveId;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.keepAliveId = readVarInt(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeVarInt(keepAliveId, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(keepAliveId) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}
}
