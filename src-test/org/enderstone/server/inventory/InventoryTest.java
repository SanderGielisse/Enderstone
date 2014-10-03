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
import junit.framework.Assert;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.util.FixedSizeList;

public class InventoryTest {

	@org.junit.Test
	public void simpleLeftMouseClick() {
		List<ItemStack> cursor = new FixedSizeList<>(new ItemStack[1]);
		PlayerInventory inventory = new PlayerInventory(null);
		inventory.setRawItem(9, new ItemStack(BlockId.STONE, (byte) 9));
		inventory.onItemClick(true, null, 9, false, cursor);
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 9), cursor.get(0));
		Assert.assertEquals(null, inventory.getRawItem(9));
	}

	@org.junit.Test
	public void simpleLeftMouseClickWithSameExistingStack() {
		List<ItemStack> cursor = new FixedSizeList<>(new ItemStack[]{new ItemStack(BlockId.STONE, (byte) 9)});
		PlayerInventory inventory = new PlayerInventory(null);
		inventory.setRawItem(9, new ItemStack(BlockId.STONE, (byte) 9));
		inventory.onItemClick(true, null, 9, false, cursor);
		Assert.assertEquals(null, cursor.get(0));
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 18), inventory.getRawItem(9));
	}

	@org.junit.Test
	public void simpleLeftMouseClickWithOtherExistingStack() {
		List<ItemStack> cursor = new FixedSizeList<>(new ItemStack[]{new ItemStack(BlockId.DIAMOND_BLOCK, (byte) 9)});
		PlayerInventory inventory = new PlayerInventory(null);
		inventory.setRawItem(9, new ItemStack(BlockId.STONE, (byte) 9));
		inventory.onItemClick(true, null, 9, false, cursor);
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 9), cursor.get(0));
		Assert.assertEquals(new ItemStack(BlockId.DIAMOND_BLOCK, (byte) 9), inventory.getRawItem(9));
	}

	@org.junit.Test
	public void simpleRigthtMouseClick() {
		List<ItemStack> cursor = new FixedSizeList<>(new ItemStack[1]);
		PlayerInventory inventory = new PlayerInventory(null);
		inventory.setRawItem(9, new ItemStack(BlockId.STONE, (byte) 9));
		inventory.onItemClick(false, null, 9, false, cursor);
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 5), cursor.get(0));
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 4), inventory.getRawItem(9));
	}

	@org.junit.Test
	public void simpleRigthtMouseClickWithSameExistingStack() {
		List<ItemStack> cursor = new FixedSizeList<>(new ItemStack[]{new ItemStack(BlockId.STONE, (byte) 9)});
		PlayerInventory inventory = new PlayerInventory(null);
		inventory.setRawItem(9, new ItemStack(BlockId.STONE, (byte) 9));
		inventory.onItemClick(false, null, 9, false, cursor);
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 8), cursor.get(0));
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 10), inventory.getRawItem(9));
	}
	
	@org.junit.Test
	public void simpleRigthtMouseClickWithOtherExistingStack() {
		List<ItemStack> cursor = new FixedSizeList<>(new ItemStack[]{new ItemStack(BlockId.DIAMOND_BLOCK, (byte) 9)});
		PlayerInventory inventory = new PlayerInventory(null);
		inventory.setRawItem(9, new ItemStack(BlockId.STONE, (byte) 9));
		inventory.onItemClick(false, null, 9, false, cursor);
		Assert.assertEquals(new ItemStack(BlockId.STONE, (byte) 9), cursor.get(0));
		Assert.assertEquals(new ItemStack(BlockId.DIAMOND_BLOCK, (byte) 9), inventory.getRawItem(9));
	}
}
