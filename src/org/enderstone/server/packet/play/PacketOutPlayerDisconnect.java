package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.chat.Message;
import org.enderstone.server.packet.Packet;

public class PacketOutPlayerDisconnect extends Packet {
	private String reason;
	private Message message;

	public PacketOutPlayerDisconnect() {
	}

	public PacketOutPlayerDisconnect(String reason) {
		this.reason = reason;
	}
	
	public PacketOutPlayerDisconnect(Message reason) {
		this.message = reason;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		if(this.reason == null) this.reason = message.toMessageJson();
		writeString(reason, buf);
	}

	@Override
	public int getSize() throws IOException {
		if(this.reason == null) this.reason = message.toMessageJson();
		return getStringSize(reason) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x40;
	}

}
