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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.enderstone.server.api.messages.CachedMessage;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.util.FixedSizeList;
import org.enderstone.server.util.MergedList;

public abstract class DefaultHalfInventory implements HalfInventory {

	protected final int size;
	protected final InventoryType type;
	protected final List<ItemStack> items;
	protected final CachedMessage inventoryTitle;
	private final List<HalfInventoryListener> listeners;
	private boolean closed;

	protected DefaultHalfInventory(InventoryType type, final int size, HalfInventoryListener... listeners) {
		this(type, size, new SimpleMessage(type.toString()), listeners);
	}

	protected DefaultHalfInventory(InventoryType type, final int size, Message inventoryTitle, HalfInventoryListener... listeners) {
		if (size > 255)
			throw new IllegalArgumentException("Minecraft protocol limitation! size must be < 256 (" + size + ")");
		this.type = type;
		this.size = size;
		this.items = new InventoryWrapper(size);
		this.inventoryTitle = CachedMessage.wrap(inventoryTitle);
		this.listeners = new CopyOnWriteArrayList<>(listeners);
	}

	@Override
	public void setRawItem(int slotNumber, ItemStack stack) {
		this.items.set(slotNumber, stack);
	}

	@Override
	public ItemStack getRawItem(int slotNumber) {
		return this.items.get(slotNumber);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public CachedMessage getTitle() {
		return inventoryTitle;
	}

	@Override
	public List<ItemStack> getRawItems() {
		return items;
	}

	@Override
	public InventoryType getType() {
		return this.type;
	}

	@Override
	public void clearInventory() {
		for (int i = 0; i < getSize(); i++) this.setRawItem(i, null);
	}

	@Override
	public void close() {
		if (closed) return;
		this.close0();
		this.closed = true;
		Iterator<FullInventory> inv = fullInventories.iterator();
		FullInventory next;
		while (inv.hasNext()) {
			if ((next = inv.next()).isClosed())
				inv.remove();
			else
				for (InventoryListener l : next.listeners)
					l.closeInventory(next);
		}
		for (HalfInventoryListener l : this.listeners) l.closeInventory(this);
	}

	protected abstract void close0();

	protected static ItemStack tryAddItem(List<ItemStack> inventory, ItemStack stack) {
		int i = 0;
		int s = inventory.size();
		for (; i < s; i++) {
			ItemStack tmp = inventory.get(i);
			if (tmp == null) {
				inventory.set(i, stack);
				return null;
			}
			if (tmp.materialTypeMatches(stack)) {
				int tmpSize = tmp.getAmount();
				int maxSize = tmp.getId().getMaxStackSize();
				int moreNeeded = maxSize - tmpSize;
				if (moreNeeded >= stack.getAmount()) {
					tmp.setAmount((byte) (tmpSize + stack.getAmount()));
					inventory.set(i, tmp);
					return null;
				} else {
					stack.setAmount((byte) (stack.getAmount() - moreNeeded));
					tmp.setAmount((byte) (tmpSize + moreNeeded));
				}
			}
		}
		return stack;
	}

	@Override
	public String toString() {
		return "Inventory{" + "size=" + size + ", type=" + type + ", items=" + items + ", listeners=" + fullInventories + '}';
	}

	protected void callSlotChance(int slot, ItemStack oldStack, ItemStack newStack) {
		Iterator<FullInventory> inv = fullInventories.iterator();
		FullInventory next;
		while (inv.hasNext()) {
			if ((next = inv.next()).isClosed())
				inv.remove();
			else
				for (InventoryListener l : next.listeners)
					l.onSlotChange(next, slot, oldStack, newStack);
		}
		for (HalfInventoryListener l : this.listeners) l.onSlotChange(this, slot, oldStack, newStack);
	}

	protected void callPropertyChance(int slot, short property, short oldValue, short newValue) {
		Iterator<FullInventory> inv = fullInventories.iterator();
		FullInventory next;
		while (inv.hasNext()) {
			if ((next = inv.next()).isClosed())
				inv.remove();
			else
				for (InventoryListener l : next.listeners)
					l.onPropertyChange(next, property, oldValue, newValue);
		}
		for (HalfInventoryListener l : this.listeners) l.onPropertyChange(this, property, oldValue, newValue);
	}

	private class InventoryWrapper extends AbstractList<ItemStack> {

		private final int offset;
		private final List<ItemStack> main;

		public InventoryWrapper(int size) {
			main = new FixedSizeList<>(new ItemStack[size]);
			offset = 0;
		}

		public InventoryWrapper(List<ItemStack> items) {
			main = items;
			offset = 0;
		}

		public InventoryWrapper(List<ItemStack> parent, int offset) {
			main = parent;
			this.offset = offset;
		}

		@Override
		public ItemStack get(int index) {
			ItemStack stack = main.get(index);
			if (stack == null)
				return null;
			return stack.clone();
		}

		@Override
		public ItemStack set(int index, ItemStack newStack) {
			ItemStack oldValue = this.main.set(index, newStack);
			if (!(oldValue == null ? newStack == null : oldValue.equals(newStack)))
				DefaultHalfInventory.this.callSlotChance(index + offset, oldValue, newStack);
			return oldValue;
		}

		@Override
		public List<ItemStack> subList(int fromIndex, int toIndex) {
			return new InventoryWrapper(main.subList(fromIndex, toIndex), fromIndex);
		}

		@Override
		public int size() {
			return main.size();
		}

		@Override
		public String toString() {
			return "InventoryWrapper{" + "offset=" + offset + ", main=" + main + '}';
		}
	}

	@Override
	public Inventory openFully(HalfInventory inventory) {
		FullInventory tmp = new FullInventory(inventory, this.combineItems(inventory));
		this.fullInventories.add(tmp);
		return tmp;
	}

	@Override
	public DropType getSlotDropType(int slot) {
		return DropType.ALL_ALLOWED;
	}

	protected abstract MergedList<ItemStack> combineItems(HalfInventory inventory);

	protected final List<FullInventory> fullInventories = new ArrayList<>();

	protected class FullInventory implements Inventory {

		private final List<ItemStack> items;
		private boolean closed = false;

		public FullInventory(HalfInventory inventory, MergedList items) {
			this.items = items;
			this.listeners = new CopyOnWriteArrayList<>();
		}

		private final List<InventoryListener> listeners;

		@Override
		public void close() {
			for (InventoryListener l : this.listeners) {
				l.closeInventory(this);
			}
			this.closed = true;
		}

		@Override
		public List<ItemStack> getRawItems() {
			return items;
		}

		@Override
		public int getSize() {
			return items.size();
		}

		@Override
		public InventoryType getType() {
			return DefaultHalfInventory.this.getType();
		}

		@Override
		public void setRawItem(int slotNumber, ItemStack stack) {
			this.items.set(slotNumber, stack);
		}

		@Override
		public Message getTitle() {
			return DefaultHalfInventory.this.getTitle();
		}

		@Override
		public Inventory openFully(HalfInventory inventory) {
			return this;
		}

		@Override
		public boolean isClosed() {
			return closed;
		}

		@Override
		public ItemStack getRawItem(int slotNumber) {
			return items.get(slotNumber);
		}

		@Override
		public void addListener(InventoryListener listener) {
			if (this.closed) throw new IllegalStateException("Inventory closed");
			this.listeners.add(listener);
		}

		@Override
		public void removeListener(InventoryListener listener) {
			this.listeners.remove(listener);
		}

		@Override
		public void addListener(HalfInventoryListener listener) {
			this.addListener(HalfInventoryListeners.toInventoryListener(listener));
		}

		@Override
		public void removeListener(HalfInventoryListener listener) {
			this.removeListener(HalfInventoryListeners.toInventoryListener(listener));
		}

		@Override
		public void clearInventory() {
			for (int i = 0; i < getSize(); i++) this.setRawItem(i, null);
		}

		@Override
		public DropType getSlotDropType(int slot) {
			return DefaultHalfInventory.this.getSlotDropType(slot);
		}

		@Override // TODO Look at the slot and see if the clicked inventory is part of this inventory or not
		public List<Integer> getShiftClickLocations(int slot) {
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < getSize(); i++) {
				list.add(i);
			}
			return list;
		}

		@Override
		public void onItemClick(boolean leftMouse, int mode, int slot, boolean shiftClick, List<ItemStack> cursor) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO IMPLEMENT THIS
		}
		
		

	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void addListener(HalfInventoryListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(HalfInventoryListener listener) {
		this.listeners.remove(listener);
	}

	@Override // TODO Look at the slot and see if the clicked inventory is part of this inventory or not
	public List<Integer> getShiftClickLocations(int slot) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < getSize(); i++) {
			list.add(i);
		}
		return list;
	}

}
