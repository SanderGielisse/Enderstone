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
import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.entity.EnderPlayer;

/**
 *
 * @author Fernando
 */
public class PlayerInventory extends Inventory{

	private final EnderPlayer player;

	public PlayerInventory(EnderPlayer player) {
		super(InventoryType.INVENTORY,44);
		this.player = player;
	}
	
	public List<ItemStack> getHotbar()
	{
		return this.items.subList(36, 9);
	}
	
	public List<ItemStack> getTopInventory()
	{
		return this.items.subList(9, 27);
	}
	
	public List<ItemStack> getFullInventory()
	{
		return this.items.subList(9, 36);
	}
	
	public List<ItemStack> getArmor()
	{
		return this.items.subList(5, 4);
	}
	
	public List<ItemStack> getCraftingInputs()
	{
		return this.items.subList(1, 4);
	}
	
	public List<ItemStack> getCraftingOutputs()
	{
		return this.items.subList(0, 1);
	}
	
	@Override
	public void close0() {
		this.player.networkManager.disconnect(new SimpleMessage("Inventory closed"));
	}
	
	public boolean shiftClickHotbar(int index)
	{
		// TODO add special handling for armor
		ItemStack stack = this.getHotbar().get(index);
		if(stack == null)return false;
		int amount = stack.getAmount();
		ItemStack result = Inventory.tryAddItem(this.getTopInventory(), stack);
		if(result != null && result.getAmount() == amount)
		{
			return true;
		}
		this.getHotbar().set(index, result);
		return true;
	}
	
	public boolean shiftMainInventory(int index)
	{
		// TODO add special handling for armor
		ItemStack stack = this.getTopInventory().get(index);
		if(stack == null)return false;
		int amount = stack.getAmount();
		ItemStack result = Inventory.tryAddItem(this.getHotbar(), stack);
		if(result != null && result.getAmount() == amount)
		{
			return true;
		}
		this.getTopInventory().set(index, result);
		return true;
	}
	
	public ItemStack pickUpItem(ItemStack item)
	{
		return Inventory.tryAddItem(this.getFullInventory(), item);
	}
}
