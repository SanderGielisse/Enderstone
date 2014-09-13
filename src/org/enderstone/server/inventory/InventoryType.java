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

/**
 *
 * @author Fernando
 */
public enum InventoryType {
	CHEST("minecraft:chest"),
	CRAFTING_TABLE("minecraft:crafting_table"),
	FURNACE("minecraft:furnace"),
	DISPENSER("minecraft:dispenser"),
	ENCHANTING_TABLE("minecraft:enchanting_table"),
	BREWING_STAND("minecraft:brewing_stand"),
	VILLAGER("minecraft:villager"),
	BEACON("minecraft:beacon"),
	ANVIL("minecraft:anvil"),
	HOPPER("minecraft:hopper"),
	DROPPER("minecraft:dropper"),
	ENTITY_HORSE("EntityHorse"),
	PLAYER_INVENTORY(null);
	private final String inventoryType;

	private InventoryType(String inventoryType) {
		this.inventoryType = inventoryType;
	}

	@Override
	public String toString() {
		return "InventoryType." + this.name() + "{" + "inventoryType=" + inventoryType + '}';
	}

	public String getInventoryType() {
		return inventoryType;
	}

}
