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
package org.enderstone.server.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.play.PacketOutSetSlot;

public class Inventory {

	public enum InventoryType {
		EQUIPMENT(), CRAFTING(), INVENTORY(), HOTBAR();
	}
	
	private EnderPlayer player;

	public Inventory(EnderPlayer player) {
		this.player = player;
	}

	private final List<Integer> EQUIPMENT = Arrays.asList(new Integer[] { 5, 6, 7, 8 });
	private final List<Integer> CRAFTING = Arrays.asList(new Integer[] { 1, 2, 3, 4, 0 });
	private final List<Integer> INVENTORY = Arrays.asList(new Integer[] { 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35 });
	private final List<Integer> HOTBAR = Arrays.asList(new Integer[] { 36, 37, 38, 39, 40, 41, 42, 43, 44 });

	private final Map<Integer, ItemStack> ITEMS = new HashMap<>();

	public void setItem(InventoryType type, int slotNumber, ItemStack stack) {
		int slot = 0;
		if (type == InventoryType.EQUIPMENT) {
			slot = this.EQUIPMENT.get(slotNumber);
		} else if (type == InventoryType.CRAFTING) {
			slot = this.CRAFTING.get(slotNumber);
		} else if (type == InventoryType.INVENTORY) {
			slot = this.INVENTORY.get(slotNumber);
		} else if (type == InventoryType.HOTBAR) {
			slot = this.HOTBAR.get(slotNumber);
		}
		this.ITEMS.put(slot, stack);
		player.getNetworkManager().sendPacket(new PacketOutSetSlot((byte) 0, (short) slot, stack));
	}
	
	public void setRawItem(int slotNumber, ItemStack stack){
		this.ITEMS.put(slotNumber, stack);
		player.getNetworkManager().sendPacket(new PacketOutSetSlot((byte) 0, (short) slotNumber, stack));
	}
}
