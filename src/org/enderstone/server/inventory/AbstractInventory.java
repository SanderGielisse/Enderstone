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
	public void onItemClick(boolean leftMouse, int mode, int slot, boolean shiftClick, List<ItemStack> cursor) {
		ItemStack existingStack = this.getRawItem(slot);
		if (leftMouse) {
			DropType slotDropType = this.getSlotDropType(slot);
			if (shiftClick && slotDropType == DropType.FULL_OUT) {
				if(existingStack == null)
					return;
				ItemStack lastResult = existingStack.clone();
				do {
					shiftClickFromMainInventory(slot);
					existingStack = this.getRawItem(slot);
				} while (!lastResult.equals(existingStack));
			} else if (shiftClick) {
				shiftClickFromMainInventory(slot);
				return;
			}
			assert !shiftClick;
			switch (slotDropType) {
				case FULL_OUT: {
					if (existingStack == null) {
						return; // Nothing happens if the slot is get-only & isempty
					}
					int maxStackSize = existingStack.getId().getMaxStackSize();
					ItemStack cursorItem = cursor.get(0);
					if (cursorItem == null) {
						cursor.set(0, existingStack);
						this.setRawItem(slot, null);
					} else if (cursorItem.materialTypeMatches(existingStack)) {
						int remainingItems = maxStackSize - cursorItem.getAmount();
						if (remainingItems >= existingStack.getAmount()) {
							cursorItem.setAmount(cursorItem.getAmount() + existingStack.getAmount());
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
				case ALL_ALLOWED: {
					ItemStack cursorItem = cursor.get(0);
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
			}

		} else {
			assert leftMouse == false;

		}
	}

	private void shiftClickFromMainInventory(int slot) {
		ItemStack existingStack = this.getRawItem(slot);
		int maxStackSize = existingStack.getId().getMaxStackSize();
		int neededAmount = existingStack.getAmount();
		for (int i : this.getShiftClickLocations(slot)) {
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
		if (neededAmount > 0)
			return;
		for (int i : this.getShiftClickLocations(slot)) {
			ItemStack item = this.getRawItem(i);
			if (item == null) {
				this.setRawItem(slot, existingStack);
				this.setRawItem(slot, null);
				break;
			} else if (item.materialTypeMatches(existingStack)) {
				int remainingItems = maxStackSize - item.getAmount();
				if (remainingItems >= existingStack.getAmount()) {
					item.setAmount(item.getAmount() + existingStack.getAmount());
					this.setRawItem(slot, null);
					break;
				}
				if (remainingItems > 0) {
					item.setAmount(item.getAmount() + remainingItems);
					existingStack.setAmount(existingStack.getAmount() - remainingItems);
				}
				this.setRawItem(i, item);
			}
		}
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
}
