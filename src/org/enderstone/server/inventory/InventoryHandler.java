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

import org.enderstone.server.EnderLogger;
import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.play.PacketInClickWindow;
import org.enderstone.server.packet.play.PacketInCloseWindow;
import org.enderstone.server.packet.play.PacketInConfirmTransaction;
import org.enderstone.server.packet.play.PacketInCreativeInventoryAction;
import org.enderstone.server.packet.play.PacketInHeldItemChange;
import org.enderstone.server.packet.play.PacketOutSetSlot;

/**
 *
 * @author Fernando
 */
public class InventoryHandler {
	private final EnderPlayer player;
	
	private final Inventory.InventoryListener listener = new Inventory.InventoryListener()
	{

		@Override
		public void onSlotChange(Inventory inv, int slot, ItemStack oldStack, ItemStack newStack) {
			if(inv != activeInventory && inv != equimentInventory)
			{
				inv.removeListener(this);
				EnderLogger.warn("Removing stale inventory listener from: "+inv);
				return;
			}
			byte windowId = getWindowId(inv);
			assert windowId >= 0;
			player.networkManager.sendPacket(new PacketOutSetSlot(windowId, (short) slot, newStack));
		}

		@Override
		public void onPropertyChange(Inventory inv, short property, short oldValue, short newValue) {
			if(inv != activeInventory && inv != equimentInventory)
			{
				inv.removeListener(this);
				EnderLogger.warn("Removing stale inventory listener from: "+inv);
				return;
			}
		}

		@Override
		public void closeInventory(Inventory inv) {
			if(inv != activeInventory && inv != equimentInventory)
			{
				inv.removeListener(this);
				EnderLogger.warn("Removing stale inventory listener from: "+inv);
				return;
			}
			
		}
		
	};
	private final PlayerInventory equimentInventory;
	private Inventory activeInventory;
	private final byte playerWindowId = 0;
	private byte nextWindowId = 1;
	
	private byte getWindowId(Inventory inv)
	{
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
	
	public void recievePacket(PacketInClickWindow packet)
	{
		player.sendMessage(new SimpleMessage(packet.toString()));
		int windowId = packet.getWindowId();
		int slot = packet.getSlot();
		int button = packet.getButton();
		int actionNumber = packet.getActionNumber();
		int mode = packet.getMode();
		ItemStack itemStack = packet.getItemStack();
		if(mode == 0){
			if(button == 0){
				//normal left mouse click
			}else if(button == 1){
				//normal right mouse click
			}
		}else if(mode == 1){
			if(button == 0){
				//shift  + left mouse
			}else if(button == 1){
				//shift  + right mouse
			}
		}else if(mode == 2){
			if(button == 0){
				//number key 1
			}else if(button == 1){
				//number key 2
			}else if(button == 2){
				//number key 3
			}else if(button == 3){
				//number key 4
			}else if(button == 4){
				//number key 5
			}else if(button == 5){
				//number key 6
			}else if(button == 6){
				//number key 7
			}else if(button == 7){
				//number key 8
			}else if(button == 8){
				//number key 9
			}
		}else if(mode == 3){
			//middle mouse click
		}else if(mode == 4){
			if(button == 0 && slot != -999){
				//drop key Q
			}else if(button == 1 && slot != -999){
				//ctrl + drop key Q
			}else if(button == 0 && slot == -999){
				//left click outside inventory
			}else if(button == 1 && slot == -999){
				//right click outside inventory
			}
		}else if(mode == 5){
			if(button == 0){
				//started left or middle mouse button drag
			}else if(button == 4){
				//started right mouse drag
			}else if(button == 1){
				//add slot for left-mouse drag
			}else if(button == 5){
				//add slot for right-mouse drag
			}else if(button == 2){
				//ending left-mouse drag
			}else if(button == 6){
				//ending right-mouse drag
			}
		}else if(mode == 6){
			//double click
		}
	}
	
	public void recievePacket(PacketInConfirmTransaction packet)
	{
		player.sendMessage(new SimpleMessage(packet.toString()));
	}
	
	public void recievePacket(PacketInCloseWindow packet)
	{
		player.sendMessage(new SimpleMessage(packet.toString()));
		if(this.activeInventory != this.equimentInventory)
		{
			this.activeInventory.removeListener(listener);
			this.activeInventory = this.equimentInventory;
		}
	}
	
	public void recievePacket(PacketInCreativeInventoryAction packet)
	{
		player.sendMessage(new SimpleMessage(packet.toString()));
	}
	
	public void recievePacket(PacketInHeldItemChange packet)
	{
		player.sendMessage(new SimpleMessage(packet.toString()));
	}
	
	public ItemStack tryPickup(ItemStack stack)
	{
		return equimentInventory.pickUpItem(stack);
	}
	
	public PlayerInventory getPlayerInventory()
	{
		return this.equimentInventory;
	}
	
	public void openInventory(Inventory inv)
	{
		if(this.activeInventory != this.equimentInventory)
		{
			this.activeInventory.removeListener(listener);
			this.activeInventory = this.equimentInventory;
		}
		this.activeInventory = inv;
		this.activeInventory.addListener(listener);
		if(++this.nextWindowId < 0)
		{
			this.nextWindowId = 1;
		}
	}
}
