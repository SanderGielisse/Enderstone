package org.enderstone.server.packet.status;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketInRequest extends Packet{

	//no fields
	
	@Override
	public void read(ByteBuf buf) throws Exception {
		//none
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
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
