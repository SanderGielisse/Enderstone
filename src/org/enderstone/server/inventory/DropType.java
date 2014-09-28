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

/**
 * This class represents the <code>DropType</code> of a slot inside a inventory.
 * Every slot inside a Inventory has a defined DropType that applies how ItemStacks are handled from and to this slot.
 * @author Fernando
 * @see HalfInventory#getSlotDropType(int)
 */
public enum DropType {
	/**
	 * This represents a normal slot as found inside a chest or <code>PlayerInventory</code>.
	 * <p>
	 * A normal slot in minecraft has the following inventory actions:
	 * <p>
	 * A Left click with a filled cursor places as many items from the cursor to the target until the cursor is empty,
	 * or the slot is filled up to the max stack size
	 * <p>
	 * A Left click with a empty cursor fills the cursor
	 * <p>
	 * A rigth click with a filled cursor removes 1 from the cursor and places it on the target,
	 * but only if it can be placed on the currend existing stack, if not the stacks will be swapped.
	 * <p>
	 * A rigth mouse click with a empty cursor removes the half of the stack
	 * (and if the stack is uneven, it takes the largest piece) and places it on the cursor
	 * <p>
	 * All other <code>DropType</code>s follow the main contract defined into this
	 */
	ALL_ALLOWED, 
	
	/**
	 * This represents a crafting table output.
	 * All items need to be taken from the slot when the operation is done, 
	 * it may not split the stack to make it fit into something else 
	 */
	FULL_OUT, 
	
	/**
	 * This represents a furnace output.
	 * <p>
	 * Items will be only placed on the cursor when <code>cursorAmount + slotAmount &lt;= itemMaxStackSize</code>,
	 * but this only applies to left mouse clicking. Shift clicking will move the items to the inventory and 
	 * may leave a half size stack into the slot when the operation is done.
	 * <p>
	 * Rigth clicking on this slot splits item stack to the cursor normally if the cursor is empty,
	 * if the cursor has the same material on it when there is a rigth click on it, it does the same as a left click, 
	 * this means that it only moves the item when the above stated contition is true
	 */
	ONLY_OUT, 
	
	/**
	 * This represents a furnace fuel input.
	 * <p>
	 * This <code>DropType</code> represents a inventory slot that only accepts fuel on shift click,
	 * but works as a normal slot when placing items
	 */
	FUEL_IN,
	
	/**
	 * This represents a furnace recipe input.
	 * <p>
	 * This <code>DropType</code> represents a inventory slot that only accepts burnable object on shift click,
	 * but works as a normal slot when placing items
	 */
	FURNACE_BURNABLE_IN,
	
	/**
	 * Slot that only accepts incomings armor boots.
	 * Warning: it has undefined behavior when you force non-helmets with code into it
	 */
	ARMOR_BOOTS_ONLY,
	
	/**
	 * Slot that only accepts incomings armor leggings.
	 * Warning: it has undefined behavior when you force non-helmets with code into it
	 */
	ARMOR_LEGGINGS_ONLY,
	
	/**
	 * Slot that only accepts incomings armor chestplates.
	 * Warning: it has undefined behavior when you force non-helmets with code into it
	 */
	ARMOR_CHESTPLATE_ONLY,
	
	/**
	 * Slot that only accepts incomings armor helmets.
	 * Warning: it has undefined behavior when you force non-helmets with code into it
	 */
	ARMOR_HELMET_ONLY,
}
