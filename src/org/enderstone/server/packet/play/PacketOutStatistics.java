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
package org.enderstone.server.packet.play;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;
import org.enderstone.server.util.Pair;

public class PacketOutStatistics extends Packet {

	private int amount;
	private List<Pair<String, Integer>> entries;

	public PacketOutStatistics(String name, int value) {
		this.amount = 1;
		this.entries = new ArrayList<>();
		this.entries.add(new Pair<String, Integer>(name, value));
	}

	public PacketOutStatistics(List<Pair<String, Integer>> entries) {
		this.amount = entries.size();
		this.entries = entries;
	}

	@Override
	public void read(PacketDataWrapper buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper buf) throws IOException {
		buf.writeVarInt(amount);
		for(int i = 0; i < amount; i++){
			buf.writeString(entries.get(i).getLeft());
			buf.writeVarInt(entries.get(i).getRight());
		}
	}

	@Override
	public int getSize() throws IOException {
		int size = 0;
		for (Pair<String, Integer> pair : entries) {
			size += getStringSize(pair.getLeft()) + getVarIntSize(pair.getRight());
		}
		return size + getVarIntSize(amount) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x37;
	}
}
