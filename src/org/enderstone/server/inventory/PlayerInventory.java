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
import org.enderstone.server.util.MergedList;

/**
 *
 * @author Fernando
 */
public class PlayerInventory extends DefaultInventory{

	private final EnderPlayer player;

	public PlayerInventory(EnderPlayer player) {
		super(InventoryType.PLAYER_INVENTORY, 45);
		this.player = player;
	}
	
	private transient List<ItemStack> hotbar;
	public List<ItemStack> getHotbar()
	{
		if(hotbar != null) return hotbar;
		return hotbar = this.items.subList(36, 45);
	}
	
	private transient List<ItemStack> topInventory;
	public List<ItemStack> getTopInventory()
	{
		if(topInventory != null) return topInventory;
		return topInventory = this.items.subList(9, 36);
	}
	
	private transient List<ItemStack> fullInventory;
	public List<ItemStack> getFullInventory()
	{
		if(fullInventory != null) return fullInventory;
		return fullInventory = this.items.subList(9, 45);
	}
	
	private transient List<ItemStack> armor;
	public List<ItemStack> getArmor()
	{
		if(armor != null) return armor;
		return armor = this.items.subList(5, 9);
	}
	
	private transient List<ItemStack> craftingInputs;
	public List<ItemStack> getCraftingInputs()
	{
		if(craftingInputs != null) return craftingInputs;
		return craftingInputs = this.items.subList(1, 5);
	}
	
	private transient List<ItemStack> craftingOutputs;
	public List<ItemStack> getCraftingOutputs()
	{
		if(craftingOutputs != null) return craftingOutputs;
		return craftingOutputs = this.items.subList(0, 1);
	}
	
	private transient List<ItemStack> itemInsertionOrder;
	public List<ItemStack> getItemInsertionOrder()
	{
		if(itemInsertionOrder != null) return itemInsertionOrder;
		return itemInsertionOrder = new MergedList.Builder<ItemStack>().addList(0, getHotbar(), 0, 9).addList(9, getTopInventory(), 9, 3 * 9).build();
	}
	
	@Override
	public void close0() {
		this.player.networkManager.disconnect(new SimpleMessage("Main inventory closed (this may not happen!)"));
	}
	
	public boolean shiftClickHotbar(int index)
	{
		// TODO add special handling for armor
		ItemStack stack = this.getHotbar().get(index);
		if(stack == null)return false;
		int amount = stack.getAmount();
		ItemStack result = DefaultInventory.tryAddItem(this.getTopInventory(), stack);
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
		ItemStack result = DefaultInventory.tryAddItem(this.getHotbar(), stack);
		if(result != null && result.getAmount() == amount)
		{
			return true;
		}
		this.getTopInventory().set(index, result);
		return true;
	}
	
	public ItemStack pickUpItem(ItemStack item)
	{
		return DefaultInventory.tryAddItem(this.getItemInsertionOrder(), item);
	}
}
