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
import org.enderstone.server.inventory.armour.Armor;

public abstract class AbstractInventory implements Inventory {

	@Override
	public void onItemClick(boolean leftMouse, List<Integer> draggedSlots, int slot, boolean shiftClick, List<ItemStack> cursor) {
		if (draggedSlots != null) {
			System.out.println(draggedSlots);
			ItemStack cursorItem = cursor.get(0);
			if (cursorItem == null) return;
			int maxStackSize = cursorItem.getId().getMaxStackSize();
			int amountToSlots;
			if (leftMouse) {
				amountToSlots = cursorItem.getAmount() / draggedSlots.size();
			} else {
				amountToSlots = 1;
			}
			int itemsRemaining = Math.max(cursorItem.getAmount() / amountToSlots, 0);
			for (Integer s : draggedSlots) {
				DropType type = this.getSlotDropType(s);
				if (type != DropType.ALL_ALLOWED) continue; // TODO if new item slots are added, edit this list
				ItemStack existingItem = this.getRawItem(s);
				if (existingItem != null) {
					if (!existingItem.materialTypeMatches(cursorItem))
						continue;
					if (existingItem.getAmount() >= maxStackSize)
						continue;
				}

				int itemsToTake = Math.min(
						Math.min(maxStackSize - (existingItem == null ? 0 : existingItem.getAmount()), amountToSlots),
						cursorItem.getAmount());
				assert itemsToTake >= 0;
				if (itemsToTake > 0) {
					if (existingItem == null)
						existingItem = cursorItem.zeroSizeClone();
					existingItem.setAmount(existingItem.getAmount() + itemsToTake);
					cursorItem.setAmount(cursorItem.getAmount() - itemsToTake);
					this.setRawItem(s, existingItem);
				}
			}
			assert cursorItem.getAmount() >= itemsRemaining;
			assert cursorItem.getAmount() >= 0;
			cursor.set(0, cursorItem.getAmount() == 0 ? null : cursorItem);
			return;
		}
		ItemStack slotItemStack = this.getRawItem(slot);
		DropType slotDropType = this.getSlotDropType(slot);
		if (shiftClick && slotDropType == DropType.FULL_OUT) {
			if (slotItemStack == null)
				return;
			ItemStack lastResult = slotItemStack.clone();
			// Keep crafting util all items on the crafting table that gives the desired recipe are crafted
			do {
				shiftClickFromMainInventory(slot, true);
				slotItemStack = this.getRawItem(slot);
			} while (lastResult.equals(slotItemStack));
		} else if (shiftClick) {
			shiftClickFromMainInventory(slot, false);
			return;
		}
		assert !shiftClick;
		switch (slotDropType) {
			case FULL_OUT:
			case ONLY_OUT: {
				if (slotItemStack == null) {
					return; // Nothing happens if the slot is get-only & isempty
				}
				// DropType.FULL_OUT, the same happens with left and rigth clicks
				int maxStackSize = slotItemStack.getId().getMaxStackSize();
				ItemStack cursorItem = cursor.get(0);
				if (cursorItem == null && slotDropType == DropType.ONLY_OUT) {
					// Weird furnace behavior inside vanila minecraft
					int amout = slotItemStack.getAmount();
					int otherAmount = amout / 2;
					int cursorAmount = amout - otherAmount;
					cursor.set(0, cursorItem = slotItemStack.clone());
					cursorItem.setAmount(cursorAmount);
					if (otherAmount == 0)
						this.getRawItems().set(slot, null);
					else {
						slotItemStack.setAmount(otherAmount);
						this.getRawItems().set(slot, slotItemStack);
					}
				} else if (cursorItem == null) {
					cursor.set(0, slotItemStack);
					this.setRawItem(slot, null);
				} else if (cursorItem.materialTypeMatches(slotItemStack)) {
					int remainingItems = maxStackSize - cursorItem.getAmount();
					if (remainingItems >= slotItemStack.getAmount()) {
						cursorItem.setAmount(cursorItem.getAmount() + slotItemStack.getAmount());
						this.setRawItem(slot, null);
						cursor.set(0, cursorItem);
					}
				}
			}
			break;
			case ARMOR_BOOTS_ONLY:
			case ARMOR_CHESTPLATE_ONLY:
			case ARMOR_HELMET_ONLY:
			case ARMOR_LEGGINGS_ONLY: {
				// No need to add more code for rigth click, armor cannot stack and with a stack size of 1, 
				//  Left and rigth mouse button presses work the same
				ItemStack cursorItem = cursor.get(0);
				Armor armor = cursorItem == null ? null : Armor.fromId(cursor.get(0).getId());
				boolean isCursorValidArmorItem = armor == null ? false : armor.getDropType() == slotDropType;
				if (!isCursorValidArmorItem)
					return;
				ItemStack target = getRawItems().get(slot);
				if (cursorItem == null || target == null || !target.materialTypeMatches(cursorItem)) {
					swapItems(cursor, 0, getRawItems(), slot);
				} else {
					int cursorAmount = cursorItem.getAmount();
					int newTargetAmount = Math.min(target.getAmount() + cursorAmount, target.getId().getMaxStackSize());
					if (newTargetAmount != target.getAmount()) {
						cursorAmount -= newTargetAmount - target.getAmount();
						if (cursorAmount > 0) {
							cursorItem.setAmount(cursorAmount);
							cursor.set(0, cursorItem);
						} else cursor.set(0, null);
						target.setAmount(newTargetAmount);
						getRawItems().set(slot, target);
					}
				}
			}
			break;
			default: {
				ItemStack cursorItem = cursor.get(0);
				if (leftMouse) {
					if (cursorItem == null || slotItemStack == null || !slotItemStack.materialTypeMatches(cursorItem)) {
						swapItems(cursor, 0, getRawItems(), slot);
					} else {
						int cursorAmount = cursorItem.getAmount();
						int newTargetAmount = Math.min(slotItemStack.getAmount() + cursorAmount, slotItemStack.getId().getMaxStackSize());
						if (newTargetAmount != slotItemStack.getAmount()) {
							cursorAmount -= newTargetAmount - slotItemStack.getAmount();
							if (cursorAmount > 0) {
								cursorItem.setAmount(cursorAmount);
								cursor.set(0, cursorItem);
							} else cursor.set(0, null);
							slotItemStack.setAmount(newTargetAmount);
							getRawItems().set(slot, slotItemStack);
						}
					}
				} else {
					if (cursorItem == null && slotItemStack == null) return;
					if (cursorItem == null) {
						int amout = slotItemStack.getAmount();
						int otherAmount = amout / 2;
						int cursorAmount = amout - otherAmount;
						cursorItem = slotItemStack.clone();
						cursorItem.setAmount(cursorAmount);
						cursor.set(0, cursorItem);
						if (otherAmount == 0)
							this.setRawItem(slot, null);
						else {
							slotItemStack.setAmount(otherAmount);
							this.setRawItem(slot, slotItemStack);
						}
					} else if (slotItemStack == null || slotItemStack.materialTypeMatches(cursorItem)) {
						assert cursorItem != null;
						if (slotItemStack == null) {
							slotItemStack = cursorItem.clone();
							slotItemStack.setAmount(0);
						}
						int cursorAmount = cursorItem.getAmount();
						int newOtherAmount = Math.min(slotItemStack.getAmount() + 1, slotItemStack.getId().getMaxStackSize());
						if (newOtherAmount != slotItemStack.getAmount()) {
							cursorAmount -= 1;
							if (cursorAmount > 0) {
								cursorItem.setAmount(cursorAmount);
								cursor.set(0, cursorItem);
							} else cursor.set(0, null);
							slotItemStack.setAmount(newOtherAmount);
							this.setRawItem(slot, slotItemStack);
						}
					}
				}
			}
			break;

		}

	}

	private void shiftClickFromMainInventory(int slot, boolean onlyWholeStackOut) {
		ItemStack existingStack = this.getRawItem(slot);
		if (existingStack == null) return;
		int maxStackSize = existingStack.getId().getMaxStackSize();
		int neededAmount = existingStack.getAmount();
		List<Integer> shiftClickLocations = this.getShiftClickLocations(slot);
		for (int i : shiftClickLocations) {
			ItemStack item = this.getRawItem(i);
			if (item == null) {
				neededAmount = 0;
				break;
			} else if (item.materialTypeMatches(existingStack)) {
				int remainingItems = maxStackSize - item.getAmount();
				if (remainingItems >= neededAmount) {
					neededAmount = 0;
					break;
				}
				if (remainingItems > 0)
					neededAmount -= remainingItems;
			}
		}
		if (neededAmount > 0 && onlyWholeStackOut)
			return;
		for (int i : shiftClickLocations) {
			ItemStack item = this.getRawItem(i);
			if (item != null && item.materialTypeMatches(existingStack)) {
				int remainingItems = maxStackSize - item.getAmount();
				if (remainingItems >= existingStack.getAmount()) {
					item.setAmount(item.getAmount() + existingStack.getAmount());
					this.setRawItem(i, item);
					existingStack.setAmount(0);
				} else if (remainingItems > 0) {
					item.setAmount(item.getAmount() + remainingItems);
					existingStack.setAmount(existingStack.getAmount() - remainingItems);
					this.setRawItem(i, item);
				}

			}
		}
		if (existingStack.getAmount() != 0)
			for (int i : shiftClickLocations) {
				ItemStack item = this.getRawItem(i);
				if (item == null) {
					this.setRawItem(i, existingStack);
					break;
				}
			}
		this.setRawItem(slot, null);
	}

	private boolean swapItems(List<ItemStack> target, int targetIndex, List<ItemStack> destination, int destionationIndex) {
		ItemStack s1 = target.get(targetIndex);
		ItemStack s2 = destination.get(destionationIndex);
		if (s1 == null && s2 == null) return true;
		if (s1 == null ? s2.equals(s1) : s1.equals(s2)) return true;
		target.set(targetIndex, s2);
		destination.set(destionationIndex, s1);
		return true;
	}

	@Override
	public DropType getSlotDropType(int slot) {
		return DropType.ALL_ALLOWED;
	}
}
