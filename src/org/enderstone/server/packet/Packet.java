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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import org.enderstone.server.Vector;
import org.enderstone.server.entity.DataWatcher;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.codec.DecodeException;
import org.jnbt.NBTOutputStream;

public abstract class Packet {

	public abstract void read(PacketDataWrapper buf) throws IOException;

	public abstract void write(PacketDataWrapper buf) throws IOException;

	public abstract int getSize() throws IOException;

	public abstract byte getId();

	public void onRecieve(NetworkManager networkManager) {
	}

	public void onSend(NetworkManager networkManager) {
	}

	public static int getDataWatcherSize(DataWatcher dataWatcher) throws IOException {
		int total = 0;

		for (Entry<Integer, Object> watch : dataWatcher.getWatchedCopy().entrySet()) {
			total++;
			if (watch.getValue() instanceof Byte) {
				// byte, index
				total++;
			} else if (watch.getValue() instanceof Short) {
				total += getShortSize();
			} else if (watch.getValue() instanceof Integer) {
				total += getIntSize();
			} else if (watch.getValue() instanceof Float) {
				total += getFloatSize();
			} else if (watch.getValue() instanceof String) {
				total += getStringSize((String) watch.getValue());
			} else if (watch.getValue() instanceof ItemStack) {
				total += getItemStackSize((ItemStack) watch.getValue());
			} else if (watch.getValue() instanceof Vector) {
				total += (getIntSize() * 3);
			} else {
				throw new UnsupportedEncodingException("Type " + watch.getValue().getClass() + " cannot be part of a datawatcher.");
			}
		}
		total++;
		return total;
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
		if (stack == null) {
			total += getShortSize();
			return total;
		}
		total += (getShortSize() * 2) + 1;
		if (stack.getCompoundTag() == null) {
			return ++total;
		}
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			try (NBTOutputStream outStream = new NBTOutputStream(new DataOutputStream(out))) {
				outStream.writeTag(stack.getCompoundTag());
			} catch (Exception e) {
				e.printStackTrace();
			}
			total += out.toByteArray().length;
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static int getLocationSize() {
		return getLongSize();
	}

	public static int getUUIDSize() {
		return 16;
	}

	public void writeFully(PacketDataWrapper wrapper) throws IOException, DecodeException {
		int writerIndex = wrapper.writerIndex();
		int packetContentSize = getSize();
		int id = getId();
		int exceptedSize = getVarIntSize(packetContentSize);
		int totalSize = exceptedSize + packetContentSize;
		wrapper.ensureWritable(totalSize);

		wrapper.writeVarInt(packetContentSize);
		wrapper.writeVarInt(id);
		write(wrapper);

		int newIndex = wrapper.writerIndex();
		if (writerIndex + totalSize != newIndex) {
			throw new DecodeException("!!! Invalid send packet !!! " + "\nExcepted size: " + totalSize + " " + "\nReal size:" + (wrapper.writerIndex() - writerIndex) + " " + "\nPacket: " + this.toString());
		}
	}
}
