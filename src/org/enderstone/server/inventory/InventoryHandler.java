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
import java.util.List;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.api.event.player.PlayerDropItemEvent;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.play.PacketInClickWindow;
import org.enderstone.server.packet.play.PacketInCloseWindow;
import org.enderstone.server.packet.play.PacketInConfirmTransaction;
import org.enderstone.server.packet.play.PacketInCreativeInventoryAction;
import org.enderstone.server.packet.play.PacketInHeldItemChange;
import org.enderstone.server.packet.play.PacketInPlayerDigging;
import org.enderstone.server.packet.play.PacketOutCloseWindow;
import org.enderstone.server.packet.play.PacketOutConfirmTransaction;
import org.enderstone.server.packet.play.PacketOutOpenWindow;
import org.enderstone.server.packet.play.PacketOutSetSlot;
import org.enderstone.server.packet.play.PacketOutWindowItems;
import org.enderstone.server.packet.play.PacketOutWindowProperty;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.util.FixedSizeList;

/**
 *
 * @author Fernando
 */
public class InventoryHandler {

	private final EnderPlayer player;

	private final InventoryListener listener = new InventoryListener() {

		@Override
		public void onSlotChange(Inventory inv, int slot, ItemStack oldStack, ItemStack newStack) {
			if (inv != activeInventory && inv != equimentInventory) {
				inv.removeListener(this);
				EnderLogger.warn("Removing stale inventory listener from: " + inv);
				return;
			}
			byte windowId = getWindowId(inv);
			assert windowId >= 0;
			player.debug(windowId + "\n  s:"+slot+"\n  from:"+String.valueOf(oldStack)+"\n  to:"+String.valueOf(newStack),EnderPlayer.PlayerDebugger.INVENTORY);
			player.networkManager.sendPacket(new PacketOutSetSlot(windowId, (short) slot, newStack));
		}

		@Override
		public void onPropertyChange(Inventory inv, short property, short oldValue, short newValue) {
			if (inv != activeInventory && inv != equimentInventory) {
				inv.removeListener(this);
				EnderLogger.warn("Removing stale inventory listener from: " + inv);
				return;
			}
			byte windowId = getWindowId(inv);
			assert windowId >= 0;
			player.networkManager.sendPacket(new PacketOutWindowProperty(windowId, property, newValue));
		}

		@Override
		public void closeInventory(Inventory inv) {
			if (inv != activeInventory && inv != equimentInventory) {
				inv.removeListener(this);
				EnderLogger.warn("Removing stale inventory listener from: " + inv);
				return;
			}
			InventoryHandler.this.openInventory(null);
		}

	};

	private final PlayerInventory equimentInventory;
	private Inventory activeInventory;
	private final byte playerWindowId = 0;
	private byte nextWindowId = 1;
	private int selectedHotbarSlot = 0;
	private final List<ItemStack> itemOnCursor = new FixedSizeList<>(new ItemStack[1]);
	private final List<Integer> lastDrag = new ArrayList<>(64);
	private int dragType;

	public short getHeldItemSlot(){
		return (short) this.selectedHotbarSlot;
	}
	
	private byte getWindowId(Inventory inv) {
		if (inv == this.equimentInventory)
			return playerWindowId;
		else if (inv == this.activeInventory)
			return this.nextWindowId;
		else
			return -1;
	}

	public InventoryHandler(EnderPlayer player) {
		this.player = player;
		this.equimentInventory = new PlayerInventory(player);
		this.equimentInventory.addListener(listener);
		this.activeInventory = equimentInventory;
	}

	public void recievePacket(PacketInPlayerDigging packet) {
		switch (packet.getStatus()) {
			case 3: {
				drop(this.equimentInventory.getHotbar(), true, this.selectedHotbarSlot);
			}
			break;
			case 4: {
				drop(this.equimentInventory.getHotbar(), false, this.selectedHotbarSlot);
			}
			break;
			default:
				throw new AssertionError("packet.getStatus() == " + packet.getStatus());
		}
	}

	protected void drop(List<ItemStack> inventory, boolean dropFullStack, int slot) {
		EnderWorld world = Main.getInstance().getWorld(player);
		ItemStack stack = inventory.get(slot);
		if (stack == null)
			return;
		if (stack.getAmount() <= 1 || dropFullStack) {
			if (Main.getInstance().callEvent(new PlayerDropItemEvent(player, stack))) {
				return;
			}
			world.dropItem(player.getLocation(), stack, 10);
			inventory.set(slot, null);
		} else {
			if (Main.getInstance().callEvent(new PlayerDropItemEvent(player, stack))) {
				return;
			}
			stack.setAmount((byte) (stack.getAmount() - 1));
			ItemStack cloned = stack.clone();
			cloned.setAmount((byte) 1);
			world.dropItem(player.getLocation(), cloned, 10);
			inventory.set(slot, stack);
		}
	}

	protected boolean swapItems(List<ItemStack> target, int targetIndex, List<ItemStack> destination, int destionationIndex) {
		ItemStack s1 = target.get(targetIndex);
		ItemStack s2 = destination.get(destionationIndex);
		if (s1 == null && s2 == null) return true;
		if (s1 == null ? s2.equals(s1) : s1.equals(s2)) return true;
		target.set(targetIndex, s2);
		destination.set(destionationIndex, s1);
		return true;
	}

	public void recievePacket(PacketInClickWindow packet) {
		//player.sendMessage(new SimpleMessage(packet.toString()));
		boolean correctTransaction = handleInventoryClick(packet);
		player.debug("Cursor: "+this.itemOnCursor.get(0),EnderPlayer.PlayerDebugger.INVENTORY);
		if(!correctTransaction) player.sendMessage(new SimpleMessage("Unable to process inventory action: "+packet.toString()));
		player.networkManager.sendPacket(new PacketOutConfirmTransaction(packet.getWindowId(), packet.getActionNumber(), correctTransaction));
	}

	public boolean handleInventoryClick(PacketInClickWindow packet) {
		byte windowId = packet.getWindowId();
		int slot = packet.getSlot();
		int button = packet.getButton();
		short actionNumber = packet.getActionNumber();
		int mode = packet.getMode();
		ItemStack itemStack = packet.getItemStack();
		if (windowId != this.nextWindowId && windowId != 0) {
			player.sendMessage(new SimpleMessage("Invalid inventory interaction!"));
			return false;
		}
		if(slot == -1) return true;
		if (slot != -999 && (slot >= this.activeInventory.getSize() || slot < 0)) {
			player.networkManager.disconnect("You hacked the inventory?? " + slot, false);
			return false;
		}
		switch (mode) {
			case 0: {
				switch (button) {
					case 0: {
						//normal left mouse click
						if (slot == -999)
							drop(this.itemOnCursor, true, 0);
						else {
							this.activeInventory.onItemClick(true, null, slot, false, itemOnCursor);
						}
					}
					return true;
					case 1: {
						//normal right mouse click
						ItemStack cursor = this.itemOnCursor.get(0);
						if (slot == -999)
							drop(this.itemOnCursor, true, 0);
						else {
							this.activeInventory.onItemClick(false, null, slot, false, itemOnCursor);
						}
					}
					return true;
					default: {
						player.networkManager.disconnect("Invalid button?? " + mode + "->" + button, true);
					}
				}
			}
			break;
			case 1: {
				if (button == 0) {
					//shift  + left mouse
					this.activeInventory.onItemClick(true, null, slot, true, itemOnCursor);
					return true;
				} else if (button == 1) {
					//shift  + right mouse
					this.activeInventory.onItemClick(false, null, slot, true, itemOnCursor);
					return true;
				}
			}
			break;
			case 2: {
				// Press on number on keyboard
				int targetSlot = button;
				//swapItems(this.equimentInventory.getHotbar(), targetSlot, this.activeInventory.getRawItems(), slot);
			}
			return false;
			case 3: {
				// TODO middle mouse click
			}
			break;
			case 4: {
				switch (slot) {
					case -1:
					case -999: {
						switch (button) {
							case 0: {
								//left click outside inventory
								drop(this.itemOnCursor, true, 0);
							}
							return true;
							case 1: {
								//right click outside inventory
								drop(this.itemOnCursor, false, 0);
							}
							return true;
							default: {
								player.networkManager.disconnect("Invalid button?? m:4 -> s:-999 -> b:" + button, true);
							}
						}
					}
					break;
					default: {
						switch (button) {
							case 0: {
								//drop key Q
								FixedSizeList<ItemStack> tmp = new FixedSizeList<>(new ItemStack[1]);
								this.activeInventory.onItemClick(true, null, slot, false, tmp);
								drop(tmp, true, 0);
							}
							return true;
							case 1: {
								//ctrl + drop key Q
								FixedSizeList<ItemStack> tmp = new FixedSizeList<>(new ItemStack[1]);
								this.activeInventory.onItemClick(true, null, slot, false, tmp);
								drop(tmp, true, 0);
							}
							return true;
							default: {
								player.networkManager.disconnect("Invalid button?? m:4 -> s:" + slot + "-> b:" + button, true);
							}
						}
					}
				}
			}
			break;
			case 5: {
				if (button == 0) {
					//started left or middle mouse button drag
					this.lastDrag.clear();
					dragType = 0;
				} else if (button == 4) {
					//started right mouse drag
					this.lastDrag.clear();
					dragType = 1;
				} else if (button == 1) {
					//add slot for left-mouse drag
					if(dragType == 0 && slot < this.activeInventory.getSize() && !this.lastDrag.contains(slot))
						this.lastDrag.add(slot);
				} else if (button == 5) {
					//add slot for right-mouse drag
					if(dragType == 1 && slot < this.activeInventory.getSize() && !this.lastDrag.contains(slot))
						this.lastDrag.add(slot);
				} else if (button == 2) {
					//ending left-mouse drag
					this.activeInventory.onItemClick(true, this.lastDrag, slot, false, this.itemOnCursor);
				} else if (button == 6) {
					//ending right-mouse drag
					this.activeInventory.onItemClick(true, this.lastDrag, slot, false, this.itemOnCursor);
				} else return false;
			}
			return true;
			case 6: {
				// TODO Add double clicking in future commits
			}
			break;
			default: {
				player.networkManager.disconnect("Invalid mode?? " + mode, true);
			}
		}
		return false;
	}

	public void recievePacket(PacketInConfirmTransaction packet) {
		//player.sendMessage(new SimpleMessage(packet.toString()));
	}

	public void recievePacket(PacketInCloseWindow packet) {
		//player.sendMessage(new SimpleMessage(packet.toString()));
		if (this.activeInventory != this.equimentInventory) {
			this.activeInventory.removeListener(listener);
			this.activeInventory = this.equimentInventory;
		} else {
			assert this.activeInventory == this.equimentInventory;
			drop(this.equimentInventory.getRawItems(), true, 1);
			drop(this.equimentInventory.getRawItems(), true, 2);
			drop(this.equimentInventory.getRawItems(), true, 3);
			drop(this.equimentInventory.getRawItems(), true, 4);
		}
		this.updateInventory();
		drop(this.itemOnCursor, true, 0);
	}

	public void recievePacket(PacketInCreativeInventoryAction packet) {
		player.sendMessage(new SimpleMessage(packet.toString()));
	}

	public void recievePacket(PacketInHeldItemChange packet) {
		//player.sendMessage(new SimpleMessage(packet.toString()));
		if (packet.getSlot() > 8 || packet.getSlot() < 0) {
			player.networkManager.disconnect("NOPE", false);
			EnderLogger.warn("Player " + player.networkManager.digitalName() + " tried to exploid the server by sending a invalid held-item-index");
			return;
		}
		selectedHotbarSlot = packet.getSlot();

	}

	public ItemStack tryPickup(ItemStack stack) {
		return equimentInventory.pickUpItem(stack);
	}

	public PlayerInventory getPlayerInventory() {
		return this.equimentInventory;
	}

	public void openInventory(HalfInventory inv) {
		Inventory inventory;
		if (inv == null) 
			inventory = equimentInventory;
		else 
			inventory = inv.openFully(equimentInventory);
		this.drop(this.itemOnCursor, true, 0);
		itemOnCursor.set(0, null);
		if (activeInventory != equimentInventory) {
			activeInventory.removeListener(listener);
			activeInventory.close();
			activeInventory = equimentInventory;
		}
		activeInventory = inventory;
		if (inventory == this.equimentInventory) {
			this.player.networkManager.sendPacket(new PacketOutCloseWindow(this.nextWindowId));
			return;
		}
		if (inventory.getType() == InventoryType.PLAYER_INVENTORY) {
			throw new IllegalArgumentException("Opening of other player inventories is not supported!");
		}
		assert inv != null;
		assert inv != equimentInventory;
		assert inventory != null;
		assert inventory != equimentInventory;
		this.activeInventory.addListener(listener);
		if (++this.nextWindowId < 0) {
			this.nextWindowId = 1;
		}
		lastDrag.clear();
		dragType = -1;
		// TODO add support for horse chests
		player.debug(nextWindowId + "\n  size:"+inventory.getSize()+"",EnderPlayer.PlayerDebugger.INVENTORY);
		this.player.networkManager.sendPacket(
				new PacketOutOpenWindow(
						this.nextWindowId, 
						inventory.getType(), 
						inventory.getTitle(), 
						(byte) (inventory.getType().getPacketSize() == -1 ? inv.getSize() : inventory.getType().getPacketSize()),
						0)
		);
		boolean isNonEmpty = false;
		int size = inv.getSize();
		List<ItemStack> items = inventory.getRawItems();
		for (int i = 0; i < size && !isNonEmpty; i++)
			if (items.get(i) != null)
				isNonEmpty = true;
		if (isNonEmpty) updateRemoteInventory();
	}

	public void updateInventory() {
		this.player.networkManager.sendPacket(new PacketOutWindowItems(playerWindowId, equimentInventory.getRawItems().toArray(new ItemStack[equimentInventory.getSize()])));
	}
	
	public void updateRemoteInventory() {
		this.player.networkManager.sendPacket(new PacketOutWindowItems(nextWindowId, activeInventory.getRawItems().toArray(new ItemStack[activeInventory.getSize()])));
	}

	public void decreaseItemInHand(int i) {
		int slot = this.selectedHotbarSlot;
		ItemStack oldStack = this.getPlayerInventory().getHotbar().get(slot);
		oldStack.setAmount(oldStack.getAmount() - i);
		if (oldStack.getAmount() <= 0) {
			this.getPlayerInventory().getHotbar().set(slot, null);
		} else {
			this.getPlayerInventory().getHotbar().set(slot, oldStack);
		}
	}

	public ItemStack getItemInHand() {
		return this.getPlayerInventory().getHotbar().get(this.selectedHotbarSlot);
	}
}
