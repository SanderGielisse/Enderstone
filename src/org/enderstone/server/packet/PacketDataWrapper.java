/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.enderstone.server.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.UUID;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Vector;
import org.enderstone.server.entity.DataWatcher;
import org.enderstone.server.inventory.ItemStack;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;

public class PacketDataWrapper {

	private final NetworkManager networkManager;
	private final ByteBuf buffer;

	public PacketDataWrapper(NetworkManager networkManager, ByteBuf buffer) {
		this.networkManager = networkManager;
		this.buffer = buffer;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public ByteBuf getBuffer() {
		return buffer;
	}

	public int readableBytes() {
		return this.buffer.readableBytes();
	}

	public void markReaderIndex() {
		this.buffer.markReaderIndex();
	}

	public void resetReaderIndex() {
		this.buffer.resetReaderIndex();
	}

	public byte readByte() {
		return this.buffer.readByte();
	}

	public int readVarInt() {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = this.buffer.readByte();

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

	public void discardSomeReadBytes() {
		this.buffer.discardSomeReadBytes();
	}

	public void readBytes(byte[] compressedPacket) {
		this.buffer.readBytes(compressedPacket);
	}

	public void writeVarInt(int value) {
		int part;
		while (true) {
			part = value & 0x7F;

			value >>>= 7;
			if (value != 0) {
				part |= 0x80;
			}

			this.buffer.writeByte(part);

			if (value == 0) {
				break;
			}
		}
	}

	public void writeBytes(ByteBuf temporarilyBuf) {
		this.buffer.writeBytes(temporarilyBuf);
	}

	public void writeBytes(ByteBuf buffer, int i, int dataSize) {
		this.buffer.writeBytes(buffer, i, dataSize);
	}

	public void writeBytes(byte[] uncompressedPacket) {
		this.buffer.writeBytes(uncompressedPacket);
	}

	public String readString() {
		int len = this.readVarInt();

		byte[] b = new byte[len];
		this.readBytes(b);

		try {
			return new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error("No UTF-8 support? This server platform is not supported!", e);
		}
	}

	public void writeString(String s) {
		byte[] b = null;
		try {
			b = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error("No UTF-8 support? This server platform is not supported!", e);
		}
		writeVarInt(b.length);
		this.writeBytes(b);
	}

	public short readShort() {
		return this.buffer.readShort();
	}

	public void writeShort(int value) {
		this.buffer.writeShort(value);
	}

	public void writeBytes(byte[] publicKeyBytes, int i, int length) {
		this.buffer.writeBytes(publicKeyBytes, i, length);
	}

	public float readFloat() {
		float f = this.buffer.readFloat();
		if (Float.isNaN(f)) {
			disconnect();
			return 0F;
		}
		return f;
	}

	public Location readLocation() {
		long value = this.readLong();
		long x = value >> 38;
		long y = value << 26 >> 52;
		long z = value << 38 >> 38;
		return new Location(null, x, y, z, 0F, 0F);
	}

	public long readLong() {
		return this.buffer.readLong();
	}

	public int writerIndex() {
		return this.buffer.writerIndex();
	}

	public void ensureWritable(int i) {
		this.buffer.ensureWritable(i);
	}

	public ItemStack readItemStack() {
		short blockId = this.readShort();
		if (blockId == -1) {
			return null;
		}

		ItemStack stack = new ItemStack(blockId, (byte) -1, (short) -1);

		stack.setAmount(this.readByte());
		stack.setDamage(this.readShort());

		int index = this.readerIndex();
		if (this.readByte() == 0) {
			return stack;
		}
		this.readerIndex(index);

		try (NBTInputStream nbtInStream = new NBTInputStream(new DataInputStream(new ByteBufInputStream(this.buffer)))) {
			stack.setCompoundTag((CompoundTag) nbtInStream.readTag());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stack;
	}

	private void readerIndex(int index) {
		this.buffer.readerIndex(index);
	}

	private int readerIndex() {
		return this.buffer.readerIndex();
	}

	public boolean readBoolean() {
		return this.buffer.readBoolean();
	}

	public int readUnsignedByte() {
		return this.buffer.readUnsignedByte();
	}

	public double readDouble() {
		double value =  this.buffer.readDouble();
		if(Double.isNaN(value)){
			disconnect();
			return 0.0D;
		}
		return value;
	}

	public void readBytes(byte[] data, int i, int length) {
		this.buffer.readBytes(data, i, length);
	}

	public void writeByte(int value) {
		this.buffer.writeByte(value);
	}

	public void writeLocation(Location loc) {
		this.writeLong(((long) loc.getBlockX() & 0x3FFFFFF) << 38 | ((long) loc.getBlockY() & 0xFFF) << 26 | (loc.getBlockZ() & 0x3FFFFFF));
	}

	public void writeLong(long l) {
		this.buffer.writeLong(l);
	}

	public void writeInt(int i) {
		this.buffer.writeInt(i);
	}

	public void writeBoolean(boolean bool) {
		this.buffer.writeBoolean(bool);
	}

	public void writeDataWatcher(DataWatcher watcher) throws UnsupportedEncodingException {
		for (Entry<Integer, Object> watch : watcher.getWatchedCopy().entrySet()) {
			if (watch.getValue() instanceof Byte) {
				// byte, index
				int i = (0 << 5 | watch.getKey() & 0x1F) & 0xFF;
				this.writeByte(i);
				this.writeByte((byte) watch.getValue());
			} else if (watch.getValue() instanceof Short) {
				int i = (1 << 5 | watch.getKey() & 0x1F) & 0xFF;
				this.writeByte(i);
				this.writeShort((short) watch.getValue());
			} else if (watch.getValue() instanceof Integer) {
				int i = (2 << 5 | watch.getKey() & 0x1F) & 0xFF;
				this.writeByte(i);
				this.writeInt((int) watch.getValue());
			} else if (watch.getValue() instanceof Float) {
				int i = (3 << 5 | watch.getKey() & 0x1F) & 0xFF;
				this.writeByte(i);
				this.writeFloat((float) watch.getValue());
			} else if (watch.getValue() instanceof String) {
				int i = (4 << 5 | watch.getKey() & 0x1F) & 0xFF;
				this.writeByte(i);
				writeString((String) watch.getValue());
			} else if (watch.getValue() instanceof ItemStack) {
				int i = (5 << 5 | watch.getKey() & 0x1F) & 0xFF;
				this.writeByte(i);
				this.writeItemStack((ItemStack) watch.getValue());
			} else if (watch.getValue() instanceof Vector) {
				int i = (6 << 5 | watch.getKey() & 0x1F) & 0xFF;
				this.writeByte(i);
				Vector vector = (Vector) watch.getValue();
				this.writeInt(vector.getX());
				this.writeInt(vector.getY());
				this.writeInt(vector.getZ());
			} else {
				throw new UnsupportedEncodingException("Type " + watch.getValue().getClass() + " cannot be part of a datawatcher.");
			}
		}
		this.writeByte(127);
	}

	public void writeItemStack(ItemStack stack) {
		if (stack == null) {
			this.writeShort(-1);
			return;
		}
		this.writeShort(stack.getBlockId());
		this.writeByte(stack.getAmount());
		this.writeShort(stack.getDamage());

		if (stack.getCompoundTag() == null) {
			this.writeByte(0);
			return;
		}
		try (NBTOutputStream outStream = new NBTOutputStream(new DataOutputStream(new ByteBufOutputStream(this.buffer)))) {
			outStream.writeTag(stack.getCompoundTag());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeFloat(float value) {
		this.buffer.writeFloat(value);
	}

	public void writeUUID(UUID uuid) {
		this.writeLong(uuid.getMostSignificantBits());
		this.writeLong(uuid.getLeastSignificantBits());
	}

	public void writeDouble(double value) {
		this.buffer.writeDouble(value);
	}
	
	private void disconnect() {
		this.networkManager.disconnect("You were disconnected for sending invalid data to the server.", true);
		EnderLogger.warn((networkManager.player == null ? networkManager.toString() : networkManager.player.getName()) + " was disconnected for sending invalid data to the server.");
	}
}
