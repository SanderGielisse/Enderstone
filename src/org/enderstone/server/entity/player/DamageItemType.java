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
package org.enderstone.server.entity.player;

import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;

public enum DamageItemType {
	
	WOODEN_SWORD(BlockId.WOOD_SWORD, 5F),
	GOLDEN_SWORD(BlockId.GOLD_SWORD, 5F),
	STONE_SWORD(BlockId.STONE_SWORD, 6F),
	IRON_SWORD(BlockId.IRON_SWORD, 7F),
	DIAMOND_SWORD(BlockId.DIAMOND_SWORD, 8F),
	
	IRON_SHOVEL(BlockId.IRON_SPADE, 3F),
	DIAMOND_SHOVEL(BlockId.DIAMOND_SPADE, 4F),
	GOLDEN_SHOVEL(BlockId.GOLD_SPADE, 1F),
	STONE_SHOVEL(BlockId.STONE_SPADE, 2F),
	WOOD_SHOVEL(BlockId.WOOD_SPADE, 1F),
	
	IRON_PICKAXE(BlockId.IRON_PICKAXE, 4F),
	DIAMOND_PICKAXE(BlockId.DIAMOND_PICKAXE, 5F),
	GOLDEN_PICKAXE(BlockId.GOLD_PICKAXE, 2F),
	STONE_PICKAXE(BlockId.STONE_PICKAXE, 3F),
	WOOD_PICKAXE(BlockId.WOOD_PICKAXE, 2F),
	
	IRON_AXE(BlockId.WOOD_AXE, 5F),
	DIAMOND_AXE(BlockId.WOOD_AXE, 6F),
	GOLDEN_AXE(BlockId.WOOD_AXE, 3F),
	STONE_AXE(BlockId.WOOD_AXE, 4F),
	WOOD_AXE(BlockId.WOOD_AXE, 3F),
	;
	
	private final BlockId blockId;
	private final float damage;

	private DamageItemType(BlockId blockId, float damage) {
		this.blockId = blockId;
		this.damage = damage;
	}

	public BlockId getBlockId() {
		return blockId;
	}

	public float getDamage() {
		return damage;
	}

	// performance boost
	private static DamageItemType[] array;
	static {
		array = values();
	}

	public static DamageItemType[] getAll() {
		return array;
	}

	public static float fromItemStack(ItemStack stack) {
		if (stack == null) {
			return 1F;
		}
		for (DamageItemType item : getAll()) {
			if (item.getBlockId().getId() == stack.getBlockId()) {
				return item.getDamage();
			}
		}
		return 1F;
	}
}
