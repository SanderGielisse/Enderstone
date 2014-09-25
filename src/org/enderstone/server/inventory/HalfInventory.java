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
 *
 * @author Fernando
 */
public interface HalfInventory extends AutoCloseable{

	public ItemStack getRawItem(int slotNumber);

	public List<ItemStack> getRawItems();

	public int getSize();

	public InventoryType getType();

	public void setRawItem(int slotNumber, ItemStack stack);

	public Message getTitle();

	public Inventory openFully(PlayerInventory inventory);
	
	public void addListener(HalfInventoryListener listener);
	
	public void removeListener(HalfInventoryListener listener);
	
	public boolean isClosed();
	
	public void clearInventory();
	
	@Override
	public void close();
	
	public DropType getSlotDropType(int slot);
	
	public List<Integer> getShiftClickLocations(int slot);

}
