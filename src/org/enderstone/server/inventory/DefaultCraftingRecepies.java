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

/**
 *
 * @author Fernando
 */
public class DefaultCraftingRecepies {

	public final static List<CraftingListener> recepies;

	static {
		List<SimpleRecipe>[] tmp = (List<SimpleRecipe>[]) new List<?>[3 * 3];
		
		
		r(tmp, BlockId.BUTTON,
				new BlockId[]{BlockId.STONE}
		);
		r(tmp, BlockId.DIAMOND_BLOCK,
				new BlockId[]{BlockId.DIAMOND, BlockId.DIAMOND, BlockId.DIAMOND},
				new BlockId[]{BlockId.DIAMOND, BlockId.DIAMOND, BlockId.DIAMOND},
				new BlockId[]{BlockId.DIAMOND, BlockId.DIAMOND, BlockId.DIAMOND}
		);
		r(tmp, BlockId.DIAMOND_BLOCK,
				new BlockId[]{BlockId.GRASS, BlockId.DIRT},
				new BlockId[]{BlockId.GRASS, BlockId.GRASS}
		);
		
		
		List<CraftingListener> listener = new ArrayList<>();
		for (final List<SimpleRecipe> r : tmp) {
			if(r == null) continue;
			final SimpleRecipe t = r.get(0);
			listener.add(new CraftingListener() {

				@Override
				public boolean acceptRecipe(int xSize, int zSize) {
					return t.acceptRecipe(xSize, zSize);
				}

				@Override
				public ItemStack checkRecipe(List<ItemStack> items, int xSize, int zSize, boolean decreaseItems) {
					for(SimpleRecipe recipe : r)
					{
						ItemStack result = recipe.checkRecipe(items, xSize, zSize, decreaseItems);
						if(result != null) return result;
					}
					return null;
				}
			});
		}
		recepies = Collections.unmodifiableList(listener);
	}

	private static void r(List<SimpleRecipe>[] tmp, ItemStack result, ItemStack[] ... items)
	{
		SimpleRecipe r = new SimpleRecipe(result, items);
		int hash = r.getxSize() * 3 + r.getzSize();
		List<SimpleRecipe> rr = tmp[hash];
		if (rr == null) tmp[hash] = rr = new ArrayList<>();
		rr.add(r);
	}

	private static void r(List<SimpleRecipe>[] tmp, BlockId result, BlockId[] ... items)
	{
		ItemStack[][] items1 = new ItemStack[items.length][];
		for (int i = 0; i < items.length; i++) {
			items1[i] = new ItemStack[items[i].length];
			for (int j = 0; j < items[i].length; j++) {
				items1[i][j] = new ItemStack(items[i][j]);
			}
		}
		r(tmp, new ItemStack(result), items1);
	}
}
