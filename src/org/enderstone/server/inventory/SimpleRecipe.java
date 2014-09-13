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
import java.util.List;

/**
 *
 * @author Fernando
 */
public class SimpleRecipe implements CraftingListener {

	private final int xSize;
	private final int zSize;
	private final List<ItemStack> neededItems;
	private final ItemStack result;

	public SimpleRecipe(List<ItemStack> items, int xSize, int zSize, ItemStack result) {
		if (items.size() != xSize * zSize) throw new IllegalArgumentException("Invalid item size");
		neededItems = items;
		this.result = result;
		this.xSize = xSize;
		this.zSize = zSize;
	}

	public SimpleRecipe(ItemStack result, ItemStack[] ... crafting)
	{
		zSize = crafting.length;
		xSize = crafting[0].length;
		for (int i = 1; i < zSize; i++) {
			if (crafting[i].length != xSize) {
				throw new IllegalArgumentException("Invalid itemstack sizes");
			}
		}
		ItemStack[] items = new ItemStack[xSize * zSize];
		for(int i = 0; i < zSize; i++)
			System.arraycopy(crafting[i], 0, items, i * xSize, xSize);
		this.neededItems = Arrays.asList(items);
		this.result = result;
	}

	public int getxSize() {
		return xSize;
	}

	public int getzSize() {
		return zSize;
	}

	public List<ItemStack> getNeededItems() {
		return neededItems;
	}

	public ItemStack getResult() {
		return result;
	}
	
	@Override
	public boolean acceptRecipe(int xSize, int zSize) {
		return xSize == this.xSize && zSize == this.zSize;
	}

	@Override
	public ItemStack checkRecipe(List<ItemStack> items, int xSize, int zSize, boolean decreaseItems) {
		if(decreaseItems) {
			for(int i = 0; i < xSize * zSize; i++) {
				ItemStack tmp = items.get(i);
				if(tmp.getAmount() < 2) {
					tmp = null;
				} else {
					tmp.setAmount(tmp.getAmount() - 1);
				}
				items.set(i, tmp);
			}
		}
		for (int i = 0; i < xSize * zSize; i++) {
			if (!items.get(i).materialTypeMatches(neededItems.get(i))) return null;
		}
		return result;
	}

}
