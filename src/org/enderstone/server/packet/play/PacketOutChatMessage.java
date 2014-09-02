package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.chat.Message;
import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.packet.Packet;

public class PacketOutChatMessage extends Packet {

	private Message message;
	private String jsonChat;
	private byte position;

	public PacketOutChatMessage(String chatMessage, boolean json, byte position) {
		if (json) {
			this.jsonChat = chatMessage;
		} else {
			this.message = new SimpleMessage(chatMessage);
		}
		this.position = position;
	}
	
	public PacketOutChatMessage(Message message, byte position)
	{
		this.message = message;
		this.position = position;
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		if(this.jsonChat == null) this.jsonChat = message.toMessageJson();
		if (getStringSize(jsonChat) > 32767) {
			throw new IllegalArgumentException("The chat messages can't be any longer than 32767 bytes!");
		}
		writeString(this.jsonChat, buf);
		buf.writeByte(getId());
	}

	@Override
	public int getSize() throws IOException {
		if(this.jsonChat == null) this.jsonChat = message.toMessageJson();
		return getStringSize(this.jsonChat) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x02;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}
}
