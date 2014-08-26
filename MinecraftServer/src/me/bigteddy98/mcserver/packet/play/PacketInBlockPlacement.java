package me.bigteddy98.mcserver.packet.play;

import io.netty.buffer.ByteBuf;
import me.bigteddy98.mcserver.inventory.ItemStack;
import me.bigteddy98.mcserver.packet.Packet;

public class PacketInBlockPlacement extends Packet {

	private int x;
	private byte y;
	private int z;
	private byte direction;
	private ItemStack heldItem;
	private byte cursorX;
	private byte cursorY;
	private byte cursorZ;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.x = buf.readInt();
		this.y = buf.readByte();
		this.z = buf.readInt();
		this.direction = buf.readByte();
		this.heldItem = readItemStack(buf);
		this.cursorX = buf.readByte();
		this.cursorY = buf.readByte();
		this.cursorZ = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return (getIntSize() * 2) + 5 + getItemStackSize(this.heldItem) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x08;
	}

	public int getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public byte getDirection() {
		return direction;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}

	public byte getCursorX() {
		return cursorX;
	}

	public byte getCursorY() {
		return cursorY;
	}

	public byte getCursorZ() {
		return cursorZ;
	}
}
