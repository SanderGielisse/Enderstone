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
import org.enderstone.server.api.messages.Message;

/**
 * A HalfInventory is basicly the half of a opened Inventory. It is usually
 * combined with a PlayerInventory using the
 * <code>openFully(HalfInventory)</code> method to get a full inventory that a
 * player can see
 *
 * @author Fernando
 */
public interface HalfInventory extends AutoCloseable {

	/**
	 * Gets a ItemStack at a specific index
	 *
	 * Warning: This method should return a clone of the orginal item, to save
	 * the ItemStack use <code>setRawItem</code>
	 *
	 * @param slotNumber the inventory index
	 * @return a clone of the itemstack at the specified index
	 */
	public ItemStack getRawItem(int slotNumber);

	/**
	 * Get all items that belong to this HalfInventory. Warning: Changes to this
	 * list may not always reflect the actual inventory
	 *
	 * @return
	 */
	public List<ItemStack> getRawItems();

	/**
	 * Get the amount of Items this Inventory has
	 *
	 * @return
	 */
	public int getSize();

	/**
	 * Returns the type inventory this HalfInventory belongs to
	 *
	 * @return
	 */
	public InventoryType getType();

	/**
	 * Sets an ItemStack inside this HalfInventory
	 *
	 * @param slotNumber
	 * @param stack
	 */
	public void setRawItem(int slotNumber, ItemStack stack);

	/**
	 * Gets the title of this inventory
	 *
	 * @return
	 */
	public Message getTitle();

	/**
	 * Opens this inventory fully
	 *
	 * @param inventory
	 * @return
	 */
	public Inventory openFully(HalfInventory inventory);

	/**
	 * Adds an Inventory listener
	 *
	 * @param listener
	 */
	public void addListener(HalfInventoryListener listener);

	/**
	 * Removes an Inventory listener
	 *
	 * @param listener
	 */
	public void removeListener(HalfInventoryListener listener);

	/**
	 * Is this inventory closed?
	 *
	 * @return
	 */
	public boolean isClosed();

	/**
	 * Clears al items from this inventory
	 */
	public void clearInventory();

	/**
	 * Closes this inventory, closing an Inventory means that no interaction can
	 * be done after it is closed. It is the task of a Inventory to update the
	 * state of <code>IsClosed()</code> to indicate if its closed or not. An
	 * Inventory can choose to ignore closing by not updating the state of
	 * isClosed()
	 */
	@Override
	public void close();

	/**
	 * Get the <code>DropType</code> of a slot. The DropType of a slot says how
	 * a player should access this slot.
	 *
	 * @param slot
	 * @return
	 */
	public DropType getSlotDropType(int slot);

	/**
	 * Gets the target locations when a certain slot is clicked in this
	 * inventory. Warning: This method shouldn't be used n a HalftInventory as
	 * it may return locations that only exists in its full Inventory
	 *
	 * @param slot the slot you need to know information about
	 * @return the locations where the item will be moved to when clicked
	 */
	public List<Integer> getShiftClickLocations(int slot);

}
