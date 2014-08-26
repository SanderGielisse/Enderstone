package me.bigteddy98.mcserver.packet.status;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInRequest extends Packet{

	//no fields
	
	@Override
	public void read(ByteBuf buf) throws Exception {
		//none
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		//none
	}

	@Override
	public int getSize() throws Exception {
		return getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}
}
