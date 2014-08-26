package me.bigteddy98.mcserver.packet;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

import me.bigteddy98.mcserver.inventory.ItemStack;

public abstract class Packet {

	public abstract void read(ByteBuf buf) throws Exception;

	public abstract void write(ByteBuf buf) throws Exception;

	public abstract int getSize() throws Exception;

	public abstract byte getId();

	public static String readString(ByteBuf buf) {
		int len = readVarInt(buf);

		byte[] b = new byte[len];
		buf.readBytes(b);

		try {
			return new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int readVarInt(ByteBuf input) {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = input.readByte();

			out |= (in & 0x7F) << (bytes++ * 7);

			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}

			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;
	}

	public static void writeString(String s, ByteBuf buf) {
		byte[] b = null;
		try {
			b = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writeVarInt(b.length, buf);
		buf.writeBytes(b);
	}

	public static void writeVarInt(int value, ByteBuf output) {
		int part;
		while (true) {
			part = value & 0x7F;

			value >>>= 7;
			if (value != 0) {
				part |= 0x80;
			}

			output.writeByte(part);

			if (value == 0) {
				break;
			}
		}
	}

	public static void writeItemStack(ItemStack stack, ByteBuf buf) {
		if (stack.getBlockId() == -1) {
			buf.writeShort(-1);
			return;
		}
		buf.writeShort(stack.getAmount());
		buf.writeShort(stack.getDamage());
		buf.writeShort(stack.getNbtLength());
		if (stack.getNbtLength() == -1) {
			return;
		}
		buf.writeBytes(stack.getNbtData());
	}

	public static ItemStack readItemStack(ByteBuf buf) {
		short blockId = buf.readShort();
		ItemStack stack = new ItemStack(blockId, (byte) -1, (short) -1, (short) -1, new byte[0]);

		if (blockId == -1) {
			return stack;
		}

		stack.setAmount(buf.readByte());
		stack.setDamage(buf.readShort());

		short nbtLength = buf.readShort();
		stack.setNbtLength(nbtLength);

		if (nbtLength == -1) {
			return stack;
		}

		byte[] data = new byte[nbtLength];
		buf.readBytes(data, 0, nbtLength);
		stack.setNbtData(data);

		return stack;
	}

	public static int getStringSize(String s) throws UnsupportedEncodingException {
		int total = 0;
		total += getVarIntSize(s.length());
		total += s.getBytes("UTF-8").length;
		return total;
	}

	public static int getVarIntSize(int value) {
		int total = 0;
		while (true) {
			value >>>= 7;
			total++;
			if (value == 0) {
				break;
			}
		}
		return total;
	}

	public static int getItemStackSize(ItemStack stack) {
		int total = 0;

		if (stack.getBlockId() == -1) {
			total += getShortSize();
			return total;
		}

		total += (getShortSize() * 3);
		if (stack.getNbtLength() == -1) {
			return total;
		}
		total += stack.getNbtData().length;
		return total;
	}

	public static int getFloatSize() {
		return 4;
	}

	public static int getIntSize() {
		return 4;
	}

	public static int getShortSize() {
		return 2;
	}

	public static int getLongSize() {
		return 8;
	}

	public static int getDoubleSize() {
		return 8;
	}
}
