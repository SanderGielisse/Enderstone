package me.bigteddy98.mcserver.packet.status;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketOutResponse extends Packet {

	private String jsonResponse;

	public PacketOutResponse() {
	}

	public PacketOutResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.jsonResponse = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		writeString(jsonResponse, buf);
	}

	@Override
	public int getSize() throws Exception {
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
