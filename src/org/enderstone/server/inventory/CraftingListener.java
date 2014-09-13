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

/**
 *
 * @author Fernando
 */
public interface CraftingListener {
	public boolean acceptRecipe(int xSize, int zSize);
	
	/**
	 * Checks this Crafting listener for items that can be crafted, items can be extracted using items.get(x + z * xSize).
	 * <p>
	 * This method should return a ItemStack to indicate the crafting was done correctly
	 * @param items The input items
	 * @param xSize The inout x grid size
	 * @param zSize The input z grid size
	 * @param decreaseItems If true, then the Crafting listener should handle the items, so that the correct item updates are fired against the incoming item list
	 * @return An Itemstack comtaining the result if the incoming item grid was used to process a recipe
	 */
	public ItemStack checkRecipe(List<ItemStack> items, int xSize, int zSize, boolean decreaseItems);
	
}
