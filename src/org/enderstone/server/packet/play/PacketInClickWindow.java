package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;

public class PacketInClickWindow extends Packet {

	private byte windowId;
	private short slot;
	private byte button;
	private short actionNumber;
	private byte mode;
	private ItemStack itemStack;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.windowId = buf.readByte();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.actionNumber = buf.readShort();
		this.mode = buf.readByte();
		this.itemStack = readItemStack(buf);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return 3 + (getShortSize() * 2) + getItemStackSize(itemStack) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0E;
	}

	public byte getWindowId() {
		return windowId;
	}

	public short getSlot() {
		return slot;
	}

	public byte getButton() {
		return button;
	}

	public short getActionNumber() {
		return actionNumber;
	}

	public byte getMode() {
		return mode;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
}
