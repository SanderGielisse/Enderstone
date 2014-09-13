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

import java.util.List;
import org.enderstone.server.util.FixedSizeList;
import org.enderstone.server.util.MergedList;

/**
 *
 * @author Fernando
 */
public class CraftingInventoryListener implements HalfInventoryListener {

	private final int xSize;
	private final int zSize;
	private final int outputSlot;
	private final int startScanSlot;
	private final int endScanSlot;
	private final List<ItemStack> cache;
	private final List<CraftingListener> listeners;
	private boolean noCraft;

	public CraftingInventoryListener(int xSize, int zSize, int outputSlot, int startScanSlot, List<CraftingListener> listeners) {
		this.xSize = xSize;
		this.zSize = zSize;
		this.outputSlot = outputSlot;
		this.startScanSlot = startScanSlot;
		this.endScanSlot = startScanSlot + (xSize * zSize);
		this.cache = new FixedSizeList<>(new ItemStack[xSize * zSize]);
		this.listeners = listeners;
	}

	protected void rescanSlot(int slot, ItemStack newStack) {
		slot -= startScanSlot;
		int x = slot % xSize;
		int z = slot / xSize;
		cache.set(slot, newStack);
	}

	protected void recalculateRecipes(HalfInventory inventory, boolean removeItems) {
		int xStart = Integer.MAX_VALUE;
		int xEnd = Integer.MIN_VALUE;
		int zStart = Integer.MAX_VALUE;
		int zEnd = Integer.MIN_VALUE;
		for (int x = 0; x < xSize; x++) {
			for (int z = 0; z < zSize; z++) {
				ItemStack tmp = this.cache.get(x + xSize * z);
				if (tmp != null) {
					if (x < xStart) xStart = x;
					if (x > xEnd) xEnd = x;
					if (z < zStart) zStart = z;
					if (z > zEnd) zEnd = z;
				}
			}
		}
		if (xStart == Integer.MAX_VALUE) {
			assert xEnd == Integer.MIN_VALUE;
			assert zStart == Integer.MAX_VALUE;
			assert zEnd == Integer.MIN_VALUE;
			ItemStack oldOut = inventory.getRawItem(outputSlot);
			if (oldOut != null) {
				setOutputSlot(inventory, null);
			}
		} else {
			assert xEnd != Integer.MIN_VALUE;
			assert zStart != Integer.MAX_VALUE;
			assert zEnd != Integer.MIN_VALUE;
			int recipeSizeX = xEnd - xStart + 1;
			int recipeSizeZ = zEnd - zStart + 1;
			List<ItemStack> assembledRecipe = null;
			ItemStack result = null;
			for (CraftingListener listener : this.listeners) {
				if (listener.acceptRecipe(recipeSizeX, recipeSizeZ)) {
					if (assembledRecipe == null) {
						MergedList.Builder<ItemStack> builder = new MergedList.Builder<>();
						for (int i = zStart; i <= zEnd; i++) {
							builder = builder.addList(recipeSizeZ * (i - zStart), cache, xStart + (zSize * i), recipeSizeX);
						}
						assembledRecipe = builder.build();
					}
					ItemStack oldResult = null;
					if(removeItems)
						oldResult = listener.checkRecipe(assembledRecipe, recipeSizeX, recipeSizeZ, true);
					result = listener.checkRecipe(assembledRecipe, recipeSizeX, recipeSizeZ, false);
					if (result != null || oldResult != null) break;
				}
			}
			if (removeItems) {
				for (int i = 0; i < this.cache.size(); i++) {
					inventory.setRawItem(this.startScanSlot + i, cache.get(i));
				}
			}
			setOutputSlot(inventory, result);
		}
	}

	private void setOutputSlot(HalfInventory inventory, ItemStack result) {
		try {
			noCraft = true;
			inventory.setRawItem(this.outputSlot, result);
		} finally {
			noCraft = false;
		}
	}

	@Override
	public void onSlotChange(HalfInventory inv, int slot, ItemStack oldStack, ItemStack newStack) {
		if (slot >= startScanSlot && slot < endScanSlot) {
			this.rescanSlot(slot, newStack == null ? null : newStack.clone());
			this.recalculateRecipes(inv, false);
		}
		if (slot == outputSlot && newStack == null && !noCraft) {
			this.recalculateRecipes(inv, true);
		}
	}

	@Override
	public void onPropertyChange(HalfInventory inv, short property, short oldValue, short newValue) {
	}

	@Override
	public void closeInventory(HalfInventory inv) {
	}
}
