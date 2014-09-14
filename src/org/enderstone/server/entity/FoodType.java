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
package org.enderstone.server.entity;

import org.enderstone.server.regions.BlockId;

public enum FoodType {
	
	APPLE(4, 2.4F, BlockId.APPLE),
	BAKED_POTATO(5, 7.2F, BlockId.BAKED_POTATO),
	BREAD(5, 6F, BlockId.BREAD),
	CARROT(3, 4.8F, BlockId.CARROT_ITEM),
	COOKED_CHICKEN(6, 7.2F, BlockId.COOKED_CHICKEN),
	COOKED_FISH(5, 6F, BlockId.COOKED_FISH),
	COOKED_MUTTON(6, 9.6F, BlockId.COOKED_MUTTON),
	COOKED_PORKCHOP(8, 12.8F, BlockId.RAW_PORK),
	COOKED_RABBIT(5, 6F, BlockId.COOKED_RABBIT),
	COOKIE(2, 0.4F, BlockId.COOKIE),
	GOLDEN_APPLE(4, 9.6F, BlockId.GOLDEN_APPLE),
	GOLDEN_CARROT(6, 14.4F, BlockId.GOLDEN_CARROT),
	MELON_SLICE(2,1.2F, BlockId.MELON),
	MUSHROOM_STEW(6, 7.2F, BlockId.MUSHROOM_SOUP),
	POISONOUS_POTATO(2, 1.2F, BlockId.POISONOUS_POTATO),
	POTATO(1, 0.6F, BlockId.POTATO),
	PUMPKIN_PIE(8, 4.8F, BlockId.PUMPKIN_PIE),
	RABBIT_STEW(10, 12F, BlockId.RABBIT_STEW),
	RAW_BEEF(3, 1.8F, BlockId.RAW_BEEF),
	RAW_CHICKEN(2, 1.2F, BlockId.RAW_CHICKEN),
	RAW_FISH(2, 0.4F, BlockId.RAW_FISH),
	RAW_MUTTON(2, 1.2F, BlockId.RAW_MUTTON),
	RAW_PORKCHOP(3, 1.8F, BlockId.RAW_PORK),
	RAW_RABBIT(2, 1.8F, BlockId.RAW_RABBIT),
	ROTTEN_FLESH(4, 0.8F, BlockId.ROTTEN_FLESH),
	SPIDER_EYE(2, 3.2F, BlockId.SPIDER_EYE),
	STEAK(8, 12.8F, BlockId.COOKED_BEEF);
	
	private final int food;
	private final float saturation;
	private final BlockId id;

	private FoodType(int food, float saturation, BlockId id) {
		this.food = food;
		this.saturation = saturation;
		this.id = id;
	}

	public int getFood() {
		return food;
	}

	public float getSaturation() {
		return saturation;
	}

	public BlockId getId() {
		return id;
	}
	
	private static final FoodType[] array;
	
	static{
		array = new FoodType[4096];
		for(FoodType type : values()){
			array[type.getId().getId()] = type;
		}
	}

	public static FoodType fromBlockId(short id) {
		return array[id];
	}
}
