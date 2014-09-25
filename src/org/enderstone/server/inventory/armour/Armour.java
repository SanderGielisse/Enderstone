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
package org.enderstone.server.inventory.armour;

import org.enderstone.server.regions.BlockId;

public enum Armour {

	LEATHER_HELMET(BlockId.LEATHER_HELMET, 20F),
	LEATHER_CHESTPLATE(BlockId.LEATHER_CHESTPLATE, 37.5F),
	LEATHER_LEGGINGS(BlockId.LEATHER_LEGGINGS, 28.6F),
	LEATHER_BOOTS(BlockId.LEATHER_BOOTS, 25F),
	
	GOLDEN_HELMET(BlockId.GOLD_HELMET, 45.8F),
	GOLDEN_CHESTPLATE(BlockId.GOLD_CHESTPLATE, 62.5F),
	GOLDEN_LEGGINS(BlockId.GOLD_LEGGINGS, 42.9F),
	GOLDEN_BOOTS(BlockId.GOLD_BOOTS, 25F),
	
	CHAIN_HELMET(BlockId.CHAINMAIL_HELMET, 40F),
	CHAIN_CHESTPLATE(BlockId.CHAINMAIL_CHESTPLATE, 62.5F),
	CHAIN_LEGGINGS(BlockId.CHAINMAIL_LEGGINGS, 57.1F),
	CHAIN_BOOTS(BlockId.CHAINMAIL_BOOTS, 25F),
	
	IRON_HELMET(BlockId.IRON_HELMET, 40F),
	IRON_CHESTPLATE(BlockId.IRON_CHESTPLATE, 75F),
	IRON_LEGGINGS(BlockId.IRON_LEGGINGS, 71.4F),
	IRON_BOOTS(BlockId.IRON_BOOTS, 50F),
	
	DIAMOND_HELMET(BlockId.DIAMOND_HELMET, 60F),
	DIAMOND_CHESTPLATE(BlockId.DIAMOND_CHESTPLATE, 85.7F),
	DIAMOND_LEGGINGS(BlockId.DIAMOND_LEGGINGS, 85.7F),
	DIAMOND_BOOTS(BlockId.DIAMOND_BOOTS, 75F),
	;
	private final BlockId blockId;
	private final float percentage;

	private Armour(BlockId blockId, float percentage) {
		this.blockId = blockId;
		this.percentage = percentage;
	}

	public BlockId getBlockId() {
		return blockId;
	}

	public float getDamageMultiplier() {
		return (120 - percentage) / 100; //120 because of 80% as a maximum
	}
	
	private final static Armour[] data = new Armour[4096];
	
	static{
		for(Armour armour : values()){
			data[armour.getBlockId().getId()] = armour;
		}
	}
	
	public Armour fromId(BlockId id){
		return data[id.getId()];
	}
}
