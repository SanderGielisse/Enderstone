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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.enderstone.server.regions.BlockId;

import static org.enderstone.server.regions.BlockId.*;
/**
 *
 * @author Fernando
 */
public class DefaultCraftingRecipes {

	public final static List<CraftingListener> recipes;

	static {
		List<SimpleRecipe>[] tmp = (List<SimpleRecipe>[]) new List<?>[3 * 3];

		r(tmp, BUTTON,
				new BlockId[]{STONE}
		);
		r(tmp, DIAMOND_BLOCK,
				new BlockId[]{DIAMOND, DIAMOND, DIAMOND},
				new BlockId[]{DIAMOND, DIAMOND, DIAMOND},
				new BlockId[]{DIAMOND, DIAMOND, DIAMOND}
		);
		r(tmp, DIAMOND_BLOCK,
				new BlockId[]{GRASS, DIRT},
				new BlockId[]{GRASS, GRASS}
		);
		r(tmp, DIAMOND, 9,
				new BlockId[]{DIAMOND_BLOCK}
		);
		
		
		//building block recipes
		
		//Andesite
		r(tmp, new ItemStack(STONE, (byte) 2, (short) 5),
				new ItemStack[]{new ItemStack(STONE, (byte) 1, (short) 3), new ItemStack(COBBLESTONE)}
		);
		
		//Polished Andesite
		r(tmp, new ItemStack(STONE, (byte) 4, (short) 6),
				new ItemStack[]{new ItemStack(STONE, (byte) 1, (short) 5), new ItemStack(STONE, (byte) 1, (short) 5)},
				new ItemStack[]{new ItemStack(STONE, (byte) 1, (short) 5), new ItemStack(STONE, (byte) 1, (short) 5)}
		);
		
		//Stone Bricks
		r(tmp, new ItemStack((short) 98, (byte) 4, (short) 0),
				new ItemStack[]{new ItemStack(STONE, (byte) 1, (short) 6), new ItemStack(STONE, (byte) 1, (short) 6)},
				new ItemStack[]{new ItemStack(STONE, (byte) 1, (short) 6), new ItemStack(STONE, (byte) 1, (short) 6)}
		);
		
		//Block of Coal
		r(tmp, new ItemStack(COAL_BLOCK, (byte) 1, (short) 0),
				new BlockId[]{COAL, COAL, COAL},
				new BlockId[]{COAL, COAL, COAL},
				new BlockId[]{COAL, COAL, COAL}
		);
		
		//Block of Diamond
		r(tmp, new ItemStack(DIAMOND_BLOCK, (byte) 1, (short) 0),
				new BlockId[]{DIAMOND, DIAMOND, DIAMOND},
				new BlockId[]{DIAMOND, DIAMOND, DIAMOND},
				new BlockId[]{DIAMOND, DIAMOND, DIAMOND}
		);

		//Block of Emerald
		r(tmp, new ItemStack(EMERALD_BLOCK, (byte) 1, (short) 0),
				new BlockId[]{EMERALD, EMERALD, EMERALD},
				new BlockId[]{EMERALD, EMERALD, EMERALD},
				new BlockId[]{EMERALD, EMERALD, EMERALD}
		);
		
		//Block of Gold
		r(tmp, new ItemStack(GOLD_BLOCK, (byte) 1, (short) 0),
				new BlockId[]{GOLD_INGOT, GOLD_INGOT, GOLD_INGOT},
				new BlockId[]{GOLD_INGOT, GOLD_INGOT, GOLD_INGOT},
				new BlockId[]{GOLD_INGOT, GOLD_INGOT, GOLD_INGOT}
		);

		//Block of Iron
		r(tmp, new ItemStack(IRON_BLOCK, (byte) 1, (short) 0),
				new BlockId[]{IRON_INGOT, IRON_INGOT, IRON_INGOT},
				new BlockId[]{IRON_INGOT, IRON_INGOT, IRON_INGOT},
				new BlockId[]{IRON_INGOT, IRON_INGOT, IRON_INGOT}
		);
		
		//Block of Quartz
		r(tmp, new ItemStack(QUARTZ_BLOCK, (byte) 1, (short) 0),
				new BlockId[]{QUARTZ, QUARTZ, QUARTZ},
				new BlockId[]{QUARTZ, QUARTZ, QUARTZ},
				new BlockId[]{QUARTZ, QUARTZ, QUARTZ}
		);
		
		//Chiseled Quartz Block
		r(tmp, new ItemStack(QUARTZ_BLOCK, (byte) 1, (short) 1),
				new ItemStack[]{new ItemStack(STEP, (byte) 1, (short) 7)},
				new ItemStack[]{new ItemStack(STEP, (byte) 1, (short) 7)}
		);
		
		//Pillar Quartz Block
		r(tmp, new ItemStack(QUARTZ_BLOCK, (byte) 2, (short) 2),
				new BlockId[]{QUARTZ_BLOCK},
				new BlockId[]{QUARTZ_BLOCK}
		);
		
		//Bookshelf
		r(tmp, BOOKSHELF,
				new BlockId[]{WOOD, WOOD, WOOD}, //TODO all types of wood allowed
				new BlockId[]{BOOK, BOOK, BOOK},
				new BlockId[]{WOOD, WOOD, WOOD} //TODO all types of wood allowed
		);
		
		//Bricks
		r(tmp, BRICK_BLOCK,
				new BlockId[]{BRICK_ITEM, BRICK_ITEM},
				new BlockId[]{BRICK_ITEM, BRICK_ITEM}
		);
		
		//Clay (Block)
		r(tmp, CLAY,
				new BlockId[]{CLAY_BALL, CLAY_BALL},
				new BlockId[]{CLAY_BALL, CLAY_BALL}
		);
		
		//Cobblestone Wall
				r(tmp, new ItemStack(COBBLE_WALL,(byte) 6, (short) 0),
						new BlockId[]{COBBLESTONE, COBBLESTONE, COBBLESTONE},
						new BlockId[]{COBBLESTONE, COBBLESTONE, COBBLESTONE}
				);
		
		//Mossy Cobblestone Wall
		r(tmp, new ItemStack(COBBLE_WALL,(byte) 6, (short) 1),
				new BlockId[]{MOSSY_COBBLESTONE, MOSSY_COBBLESTONE, MOSSY_COBBLESTONE},
				new BlockId[]{MOSSY_COBBLESTONE, MOSSY_COBBLESTONE, MOSSY_COBBLESTONE}
		);
		
		//Dark Prismarine
		r(tmp, new ItemStack(PRISMARINE, (short) 2),
				new BlockId[]{PRISMARINE_SHARD, PRISMARINE_SHARD, PRISMARINE_SHARD},
				new BlockId[]{PRISMARINE_SHARD, INK_SACK, PRISMARINE_SHARD},
				new BlockId[]{PRISMARINE_SHARD, PRISMARINE_SHARD, PRISMARINE_SHARD}
		);
		
		//Diorite
		r(tmp, new ItemStack(STONE, (byte) 2, (short) 3),
				new BlockId[]{COBBLESTONE, QUARTZ},
				new BlockId[]{QUARTZ, COBBLESTONE}
		);
		
		//Polished Diorite
		r(tmp, new ItemStack(STONE,(byte) 4, (short) 4),
				new ItemStack[]{new ItemStack(BlockId.STONE, (short) 3), new ItemStack(BlockId.STONE, (short) 3)},
				new ItemStack[]{new ItemStack(BlockId.STONE, (short) 3), new ItemStack(BlockId.STONE, (short) 3)}
		);
		
		//Stone Bricks
		r(tmp, new ItemStack(STONE_BRICK, (byte) 4),
				new ItemStack[]{new ItemStack(BlockId.STONE, (short) 4), new ItemStack(BlockId.STONE, (short) 4)},
				new ItemStack[]{new ItemStack(BlockId.STONE, (short) 4), new ItemStack(BlockId.STONE, (short) 4)}
		);
				
		//Coarse Dirt
		r(tmp, new ItemStack(DIRT, (byte) 4, (short) 1),
				new BlockId[]{GRAVEL, DIRT},
				new BlockId[]{DIRT, GRAVEL}
		);
		
		//Glowstone
		r(tmp, GLOWSTONE,
				new BlockId[]{GLOWSTONE_DUST, GLOWSTONE_DUST},
				new BlockId[]{GLOWSTONE_DUST, GLOWSTONE_DUST}
		);
		
		//Granite
		r(tmp, new ItemStack(STONE, (short) 1),
				new ItemStack[]{new ItemStack(BlockId.STONE, (short) 3), new ItemStack(BlockId.QUARTZ)}
		);
		
		//Polished Granite
		r(tmp, new ItemStack(STONE),
				new BlockId[]{GLOWSTONE_DUST, GLOWSTONE_DUST},
				new BlockId[]{GLOWSTONE_DUST, GLOWSTONE_DUST}
		);
		
		List<CraftingListener> listener = new ArrayList<>();
		for (final List<SimpleRecipe> r : tmp) {
			if (r == null) continue;
			final SimpleRecipe t = r.get(0);
			listener.add(new CraftingListener() {

				@Override
				public boolean acceptRecipe(int xSize, int zSize) {
					return t.acceptRecipe(xSize, zSize);
				}

				@Override
				public ItemStack checkRecipe(List<ItemStack> items, int xSize, int zSize, boolean decreaseItems) {
					for (SimpleRecipe recipe : r) {
						ItemStack result = recipe.checkRecipe(items, xSize, zSize, decreaseItems);
						if (result != null) return result;
					}
					return null;
				}
			});
		}
		recipes = Collections.unmodifiableList(listener);
	}

	private static void r(List<SimpleRecipe>[] tmp, ItemStack result, ItemStack[] ... items)
	{
		SimpleRecipe r = new SimpleRecipe(result, items);
		int hash = (r.getxSize() - 1) * 3 + (r.getzSize() - 1);
		List<SimpleRecipe> rr = tmp[hash];
		if (rr == null) tmp[hash] = rr = new ArrayList<>();
		rr.add(r);
	}

	private static void r(List<SimpleRecipe>[] tmp, BlockId result, int amount, BlockId[] ... items)
	{
		r(tmp, new ItemStack(result, (byte) amount), items);
	}

	private static void r(List<SimpleRecipe>[] tmp, BlockId result, BlockId[] ... items)
	{
		r(tmp, result, 1, items);
	}

	private static void r(List<SimpleRecipe>[] tmp, ItemStack result, BlockId[] ... items)
	{
		ItemStack[][] items1 = new ItemStack[items.length][];
		for (int i = 0; i < items.length; i++) {
			items1[i] = new ItemStack[items[i].length];
			for (int j = 0; j < items[i].length; j++) {
				items1[i][j] = new ItemStack(items[i][j]);
			}
		}
		r(tmp, result, items1);
	}
}
