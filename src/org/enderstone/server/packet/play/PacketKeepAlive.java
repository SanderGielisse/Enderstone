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
		this.keepAliveId = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(keepAliveId);
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}
}
