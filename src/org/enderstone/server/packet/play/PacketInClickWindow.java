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
package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInClickWindow extends Packet {

	private byte windowId;
	private short slot;
	private byte button;
	private short actionNumber;
	private byte mode;
	private ItemStack itemStack;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.windowId = buf.readByte();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.actionNumber = buf.readShort();
		this.mode = buf.readByte();
		this.itemStack = readItemStack(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return 3 + (getShortSize() * 2) + getItemStackSize(itemStack) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0E;
	}
	
	@Override
	public void onRecieve(NetworkManager networkManager) {
		EnderLogger.warn("WindowId: " + windowId + " Slot: " + slot + " Button: " + button + " ActionNumber: " + actionNumber + " Mode: " + mode + " Air: " + ((itemStack == null) || (itemStack.getBlockId() == (short)0)));
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

	public byte getWindowId() {
		return windowId;
	}

	public short getSlot() {
		return slot;
	}

	public byte getButton() {
		return button;
	}

	public short getActionNumber() {
		return actionNumber;
	}

	public byte getMode() {
		return mode;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
}
