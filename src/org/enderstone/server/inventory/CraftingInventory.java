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

import org.enderstone.server.api.Location;
import org.enderstone.server.api.messages.CachedMessage;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.util.MergedList;

/**
 *
 * @author Fernando
 */
public class CraftingInventory extends DefaultHalfInventory {

	private final Location location;
	private static final CachedMessage TITLE = CachedMessage.wrap(new SimpleMessage("Crafting Table"));

	public CraftingInventory(Location location) {
		super(InventoryType.CRAFTING_TABLE, 10, TITLE, new CraftingInventoryListener(3, 3, 0, 1, DefaultCraftingRecipes.getRecipes()));
		this.location = location;
	}

	@Override
	protected void close0() {
		for(ItemStack item : this.items) {
			if(item != null) {
				//TODO implement player drop item event
				location.getWorld().dropItem(location, item, 10);
			}
		}
	}

	@Override
	public Inventory openFully(HalfInventory inventory) {
		Inventory inv = super.openFully(inventory);
		inv.addListener(new InventoryListener() {

			@Override
			public void onSlotChange(Inventory inv, int slot, ItemStack oldStack, ItemStack newStack) {
			}

			@Override
			public void onPropertyChange(Inventory inv, short property, short oldValue, short newValue) {
			}

			@Override
			public void closeInventory(Inventory inv) {
				close();
			}
		});
		return inv;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MergedList<ItemStack> combineItems(HalfInventory inventory) {
		return new MergedList.Builder<ItemStack>().addList(0, this.items, 0, 10).addList(10, inventory.getRawItems(), 9, 36).build();
	}

	@Override
	public DropType getSlotDropType(int slot) {
		if(slot == 0) return DropType.FULL_OUT;
		return super.getSlotDropType(slot);
	}

	
	private static final List<Integer> shiftToInventory = PlayerInventory.getInventorySlotsShiftClickOrder(10);
	private static final List<Integer> shiftToInventoryHotbar = PlayerInventory.getHotbarSlotsShiftClickOrder(10);
	private static final List<Integer> shiftToInventoryMain = PlayerInventory.getMainInventorySlotsShiftClickOrder(10);
	
	@Override
	public List<Integer> getShiftClickLocations(int slot) {
		if(slot < 10)
			return shiftToInventory;
		else if(slot < 10 + (3 * 9))
			return shiftToInventoryHotbar;
		else if(slot < 10 + (4 * 9))
			return shiftToInventoryMain;
		else throw new IndexOutOfBoundsException("Invalid slot: "+slot );
	}
}
