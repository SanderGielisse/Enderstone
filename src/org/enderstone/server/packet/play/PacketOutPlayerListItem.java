package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketOutPlayerListItem extends Packet {

	private String name;
	private boolean online;
	private short ping;

	public PacketOutPlayerListItem(String name, boolean online, short ping) {
		this.name = name;
		this.online = online;
		this.ping = ping;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName()
				+ " with ID 0x" + Integer.toHexString(getId())
				+ " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		if (name.length() > 16) {
			throw new IllegalArgumentException(
					"Playername in TAB-List can't be longer than 16 characters!");
		}
		writeString(this.name, buf);
		buf.writeBoolean(this.online);
		buf.writeShort(this.ping);
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(name) + 1 + getShortSize()
				+ getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x38;
	}
}
