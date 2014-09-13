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

import java.util.Objects;

/**
 *
 * @author Fernando
 */
public class HalfInventoryListeners {

	public static InventoryListener toInventoryListener(final HalfInventoryListener listener) {
		return new InventoryListenerConvetor(listener);
	}

	private static class InventoryListenerConvetor implements InventoryListener {

		private final HalfInventoryListener listener;

		public InventoryListenerConvetor(HalfInventoryListener listener) {
			this.listener = listener;
		}

		@Override
		public void onSlotChange(Inventory inv, int slot, ItemStack oldStack, ItemStack newStack) {
			listener.onSlotChange(inv, slot, oldStack, newStack);
		}

		@Override
		public void onPropertyChange(Inventory inv, short property, short oldValue, short newValue) {
			listener.onPropertyChange(inv, property, oldValue, newValue);
		}

		@Override
		public void closeInventory(Inventory inv) {
			listener.closeInventory(inv);
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 79 * hash + Objects.hashCode(this.listener);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final InventoryListenerConvetor other = (InventoryListenerConvetor) obj;
			return Objects.equals(this.listener, other.listener);
		}

		@Override
		public String toString() {
			return "InventoryListenerConvetor{" + "listener=" + listener + '}';
		}

		public HalfInventoryListener getListener() {
			return listener;
		}

	}
}
