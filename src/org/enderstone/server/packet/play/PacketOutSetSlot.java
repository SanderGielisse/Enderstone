package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;

public class PacketOutSetSlot extends Packet {

	private byte windowId;
	private short slot;
	private ItemStack stack;

	public PacketOutSetSlot(byte windowId, short slot, ItemStack stack) {
		this.windowId = windowId;
		this.slot = slot;
		this.stack = stack;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeByte(windowId);
		buf.writeShort(slot);
		writeItemStack(stack, buf);
	}

	@Override
	public int getSize() throws IOException {
		return 1 + getShortSize() + getItemStackSize(stack) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x2F;
	}
}
