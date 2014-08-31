package org.enderstone.server.packet.status;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketPing extends Packet {

	private long time;

	public PacketPing() {
	}

	public PacketPing(long time) {
		this.time = time;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		time = buf.readLong();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeLong(time);
	}

	@Override
	public int getSize() throws IOException {
		return getLongSize() + getVarIntSize(getId());
	}

	@Override
	public void onRecieve(NetworkManager networkManager) {
		networkManager.sendPacket(this);
	}

	@Override
	public byte getId() {
		return 0x01;
	}
}
