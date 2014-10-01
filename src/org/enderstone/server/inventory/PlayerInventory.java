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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.enderstone.server.Main;
import org.enderstone.server.api.event.player.PlayerPickupItemEvent;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.inventory.armour.Armor;
import org.enderstone.server.util.MergedList;

/**
 *
 * @author Fernando
 */
public class PlayerInventory extends DefaultInventory {

	private final EnderPlayer player;

	public PlayerInventory(EnderPlayer player) {
		super(InventoryType.PLAYER_INVENTORY, 45,
				HalfInventoryListeners.toInventoryListener(
						new CraftingInventoryListener(2, 2, 0, 1, DefaultCraftingRecipes.getRecipes())));
		this.player = player;
	}

	private transient List<ItemStack> hotbar;

	public List<ItemStack> getHotbar() {
		if (hotbar != null) return hotbar;
		return hotbar = this.items.subList(36, 45);
	}

	private transient List<ItemStack> topInventory;

	public List<ItemStack> getTopInventory() {
		if (topInventory != null) return topInventory;
		return topInventory = this.items.subList(9, 36);
	}

	private transient List<ItemStack> fullInventory;

	public List<ItemStack> getFullInventory() {
		if (fullInventory != null) return fullInventory;
		return fullInventory = this.items.subList(9, 45);
	}

	private transient List<ItemStack> armor;

	public List<ItemStack> getArmor() {
		if (armor != null) return armor;
		return armor = this.items.subList(5, 9);
	}

	private transient List<ItemStack> craftingInputs;

	public List<ItemStack> getCraftingInputs() {
		if (craftingInputs != null) return craftingInputs;
		return craftingInputs = this.items.subList(1, 5);
	}

	private transient List<ItemStack> craftingOutputs;

	public List<ItemStack> getCraftingOutputs() {
		if (craftingOutputs != null) return craftingOutputs;
		return craftingOutputs = this.items.subList(0, 1);
	}

	private transient List<ItemStack> itemInsertionOrder;

	public List<ItemStack> getItemInsertionOrder() {
		if (itemInsertionOrder != null) return itemInsertionOrder;
		return itemInsertionOrder = new MergedList.Builder<ItemStack>().addList(0, getHotbar(), 0, 9).addList(9, getTopInventory(), 0, 3 * 9).build();
	}

	@Override
	public void close0() {
		this.player.networkManager.disconnect(new SimpleMessage("Main inventory closed (this may not happen!)"), true);
	}

	public boolean shiftClickHotbar(int index) {
		// TODO add special handling for armor
		ItemStack stack = this.getHotbar().get(index);
		if (stack == null) return false;
		int amount = stack.getAmount();
		ItemStack result = DefaultInventory.tryAddItem(this.getTopInventory(), stack);
		if (result != null && result.getAmount() == amount) {
			return true;
		}
		this.getHotbar().set(index, result);
		return true;
	}

	public boolean shiftMainInventory(int index) {
		// TODO add special handling for armor
		ItemStack stack = this.getTopInventory().get(index);
		if (stack == null) return false;
		int amount = stack.getAmount();
		ItemStack result = DefaultInventory.tryAddItem(this.getHotbar(), stack);
		if (result != null && result.getAmount() == amount) {
			return true;
		}
		this.getTopInventory().set(index, result);
		return true;
	}

	public ItemStack pickUpItem(ItemStack item) {
		if (Main.getInstance().callEvent(new PlayerPickupItemEvent(player, item))) {
			return item;
		}
		return DefaultInventory.tryAddItem(this.getItemInsertionOrder(), item);
	}

	/**
	 * INTERNAL USE ONLY returns the slots items must be placed in when they come from other inventories
	 *
	 * @param offset all numbers will be offset by this number
	 * @return
	 */
	static List<Integer> getInventorySlotsShiftClickOrder(int offset) {
		return getInventorySlotsShiftClickOrder(new ArrayList<Integer>(1 * 9), offset, false);
	}

	private static List<Integer> getInventorySlotsShiftClickOrder(List<Integer> l, int offset, boolean reverse) {
		if (reverse) {
			for (int i = 0; i < (4 * 9); i++) {
				l.add(i + offset);
			}
		} else
			for (int i = (4 * 9) - 1; i >= -1; i--) {
				l.add(i + offset);
			}
		return l;
	}

	/**
	 * INTERNAL USE ONLY returns the slots items must be placed in when they come from other inventories
	 *
	 * @param offset all numbers will be offset by this number
	 * @return
	 */
	static List<Integer> getHotbarSlotsShiftClickOrder(int offset) {
		return getHotbarSlotsShiftClickOrder(new ArrayList<Integer>(4 * 9), offset);
	}

	/**
	 * INTERNAL USE ONLY returns the slots items must be placed in when they come from other inventories
	 */
	private static List<Integer> getHotbarSlotsShiftClickOrder(List<Integer> l, int offset) {
		for (int i = 0; i < (1 * 9); i++) {
			l.add(i + offset + (3 * 9));
		}
		return l;
	}

	/**
	 * INTERNAL USE ONLY returns the slots items must be placed in when they come from other inventories
	 *
	 * @param offset all numbers will be offset by this number
	 * @return
	 */
	static List<Integer> getMainInventorySlotsShiftClickOrder(int offset) {
		return getMainInventorySlotsShiftClickOrder(new ArrayList<Integer>(9), offset);
	}

	private static List<Integer> getMainInventorySlotsShiftClickOrder(List<Integer> l, int offset) {
		for (int i = 0; i < (3 * 9); i++) {
			l.add(i + offset);
		}
		return l;
	}
	// TODO There has to be a better way to track all the slots where a item is placed when its being shift clicked....
	private static final List<Integer> shiftToInventory = PlayerInventory.getInventorySlotsShiftClickOrder(9);
	private static final List<Integer> shiftToInventoryReverse = PlayerInventory.getInventorySlotsShiftClickOrder(new ArrayList<Integer>(), 9, true);
	private static final List<Integer> shiftToInventoryHotbar = PlayerInventory.getHotbarSlotsShiftClickOrder(9);
	private static final List<Integer> shiftToInventoryHotbarHelmet = PlayerInventory.getHotbarSlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(5)), 9);
	private static final List<Integer> shiftToInventoryHotbarChestPlate = PlayerInventory.getHotbarSlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(6)), 9);
	private static final List<Integer> shiftToInventoryHotbarLeggings = PlayerInventory.getHotbarSlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(7)), 9);
	private static final List<Integer> shiftToInventoryHotbarBoots = PlayerInventory.getHotbarSlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(8)), 9);
	private static final List<Integer> shiftToInventoryMain = PlayerInventory.getMainInventorySlotsShiftClickOrder(9);
	private static final List<Integer> shiftToInventoryMainHelmet = PlayerInventory.getMainInventorySlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(5)), 9);
	private static final List<Integer> shiftToInventoryMainChestPlate = PlayerInventory.getMainInventorySlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(6)), 9);
	private static final List<Integer> shiftToInventoryMainLeggings = PlayerInventory.getMainInventorySlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(7)), 9);
	private static final List<Integer> shiftToInventoryMainBoots = PlayerInventory.getMainInventorySlotsShiftClickOrder(
			new ArrayList(Arrays.<Integer>asList(8)), 9);

	private static <T> List<T> addToList(List<T> list, T element) {
		list.add(element);
		return list;
	}

	@Override
	public List<Integer> getShiftClickLocations(int slot) {
		DropType itemType = DropType.ALL_ALLOWED;
		{
			ItemStack item = this.getRawItem(slot);
			if (item != null) {
				Armor armorType = Armor.fromId(item.getId());
				if (armorType != null) {
					itemType = armorType.getDropType();
				}
			}
		}
		if (itemType == null) itemType = DropType.ALL_ALLOWED;
		if (slot == 0)
			return shiftToInventory; // Why these slots are at a other order than the other slots? I don't know..
		if (slot < 9)
			return shiftToInventoryReverse;
		else if (slot < 9 + (3 * 9))
			switch (itemType) {
				case ARMOR_BOOTS_ONLY:
					return shiftToInventoryHotbarBoots;
				case ARMOR_LEGGINGS_ONLY:
					return shiftToInventoryHotbarLeggings;
				case ARMOR_CHESTPLATE_ONLY:
					return shiftToInventoryHotbarChestPlate;
				case ARMOR_HELMET_ONLY:
					return shiftToInventoryHotbarHelmet;
				default:
					return shiftToInventoryHotbar;
			}
		else if (slot < 9 + (4 * 9))
			switch (itemType) {
				case ARMOR_BOOTS_ONLY:
					return shiftToInventoryMainBoots;
				case ARMOR_LEGGINGS_ONLY:
					return shiftToInventoryMainLeggings;
				case ARMOR_CHESTPLATE_ONLY:
					return shiftToInventoryMainChestPlate;
				case ARMOR_HELMET_ONLY:
					return shiftToInventoryMainHelmet;
				default:
					return shiftToInventoryMain;
			}
		else throw new IndexOutOfBoundsException("Invalid slot: " + slot);
	}
	
	@Override
	public DropType getSlotDropType(int slot) {
		if(slot == 0) return DropType.FULL_OUT;
		if(slot == 5) return DropType.ARMOR_HELMET_ONLY;
		if(slot == 6) return DropType.ARMOR_CHESTPLATE_ONLY;
		if(slot == 7) return DropType.ARMOR_LEGGINGS_ONLY;
		if(slot == 8) return DropType.ARMOR_BOOTS_ONLY;
		return DropType.ALL_ALLOWED;
	}

}
