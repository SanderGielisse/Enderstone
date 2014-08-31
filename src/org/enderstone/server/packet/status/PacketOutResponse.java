package org.enderstone.server.packet.status;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

public class PacketOutResponse extends Packet {

	private String jsonResponse;

	public PacketOutResponse() {
	}

	public PacketOutResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.jsonResponse = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeString(jsonResponse, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(jsonResponse) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}
}
