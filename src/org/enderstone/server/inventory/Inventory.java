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

import java.util.AbstractList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.enderstone.server.util.FixedSizeList;

public abstract class Inventory implements AutoCloseable {

	protected final int size;
	protected final InventoryType type;
	protected final List<ItemStack> items;
	private final List<InventoryListener> listeners;

	protected Inventory(InventoryType type, final int size, InventoryListener... listeners) {
		this.type = type;
		this.size = size;
		this.items = new InventoryWrapper(size);
		this.listeners = new CopyOnWriteArrayList<>(listeners);
	}

	public void setRawItem(int slotNumber, ItemStack stack) {
		this.items.set(slotNumber, stack);
	}

	public void getRawItem(int slotNumber) {
		this.items.get(slotNumber);
	}

	public void addListener(InventoryListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(InventoryListener listener) {
		this.listeners.remove(listener);
	}

	protected void callSlotChance(int slot, ItemStack oldStack, ItemStack newStack) {
		for (InventoryListener l : Inventory.this.listeners) {
			l.onSlotChange(Inventory.this, slot, oldStack, newStack);
		}
	}
	
	protected void callPropertyChance(int slot, short property, short oldValue, short newValue) {
		for (InventoryListener l : Inventory.this.listeners) {
			l.onPropertyChange(this, property, oldValue, newValue);
		}
	}

	@Override
	public final void close() {
		this.close0();
		for (InventoryListener l : this.listeners) {
			l.closeInventory(this);
		}
	}

	protected abstract void close0();

	protected static ItemStack tryAddItem(List<ItemStack> inventory, ItemStack stack)
	{
		List<ItemStack> mainInv = inventory;
		int i = 0;
		int s = mainInv.size();
		for(;i < s; i++)
		{
			ItemStack tmp = mainInv.get(i);
			if(tmp == null)
			{
				mainInv.set(i, stack);
				return null;
			}
			if(tmp.materialTypeMatches(stack))
			{
				int tmpSize = tmp.getAmount();
				int maxSize = tmp.getId().getMaxStackSize();
				int moreNeeded = maxSize - tmpSize;
				if(moreNeeded >= stack.getAmount())
				{
					tmp.setAmount((byte) (tmpSize + stack.getAmount()));
					mainInv.set(i, tmp);
					return null;
				}
				else
				{
					stack.setAmount((byte) (stack.getAmount() - moreNeeded));
					tmp.setAmount((byte) (tmpSize + moreNeeded));
				}
			}
		}
		return stack;
	}
	
	private class InventoryWrapper extends AbstractList<ItemStack> {

		private final int offset;

		public InventoryWrapper(int size) {
			main = new FixedSizeList<>(new ItemStack[size]);
			offset = 0;
		}

		public InventoryWrapper(List<ItemStack> parent, int offset) {
			main = parent;
			this.offset = offset;
		}
		private final List<ItemStack> main;

		@Override
		public ItemStack get(int index) {
			return main.get(index).clone();
		}

		@Override
		public ItemStack set(int index, ItemStack newStack) {
			ItemStack oldValue = this.main.set(index, newStack);
			if (oldValue != newStack)
				Inventory.this.callSlotChance(index, oldValue, newStack);
			return oldValue;
		}

		@Override
		public List<ItemStack> subList(int fromIndex, int toIndex) {
			return new InventoryWrapper(main.subList(fromIndex, toIndex), toIndex);
		}

		@Override
		public int size() {
			return main.size();
		}
	}
	
	public interface InventoryListener {

		public void onSlotChange(Inventory inv, int slot, ItemStack oldStack, ItemStack newStack);

		public void onPropertyChange(Inventory inv, short property, short oldValue, short newValue);
		
		public void closeInventory(Inventory inv);
	}

	public enum InventoryType {

		EQUIPMENT(), CRAFTING(), INVENTORY();
	}
}
