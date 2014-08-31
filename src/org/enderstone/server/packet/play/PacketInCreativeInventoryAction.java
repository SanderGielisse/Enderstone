package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;

public class PacketInCreativeInventoryAction extends Packet {

	private short slot;
	private ItemStack clicked;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.slot = buf.readShort();
		this.clicked = readItemStack(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getShortSize() + getItemStackSize(clicked) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x10;
	}

	public short getSlot() {
		return slot;
	}

	public ItemStack getClicked() {
		return clicked;
	}
}
