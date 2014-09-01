package org.enderstone.server.packet.login;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.chat.Message;
import org.enderstone.server.packet.Packet;
import static org.enderstone.server.packet.Packet.getStringSize;
import static org.enderstone.server.packet.Packet.getVarIntSize;
import static org.enderstone.server.packet.Packet.writeString;

/**
 *
 * @author Fernando
 */
public class PacketOutLoginPlayerDisconnect extends Packet {
	private String reason;
	private Message message;

	public PacketOutLoginPlayerDisconnect() {
	}

	public PacketOutLoginPlayerDisconnect(String reason) {
		this.reason = reason;
	}
	
	public PacketOutLoginPlayerDisconnect(Message reason) {
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
		return 0x00;
	}

}