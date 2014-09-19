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

import java.io.IOException;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketInUseEntity extends Packet {

	private int targetId;
	private int mouseClick;
	private float targetX;
	private float targetY;
	private float targetZ;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.targetId = wrapper.readVarInt();
		this.mouseClick = wrapper.readVarInt();
		if (mouseClick == 2) {
			this.targetX = wrapper.readFloat();
			this.targetY = wrapper.readFloat();
			this.targetZ = wrapper.readFloat();
		}
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(targetId) + getVarIntSize(mouseClick) + ((mouseClick == 2) ? (getFloatSize() * 3) : 0) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x02;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {				
				if (mouseClick == 1) { // left click
					EnderEntity e = Main.getInstance().getEntityById(targetId);
					if (e == null) {
						return;
					}
					if (e.isDead()) {
						return;
					}
					e.onLeftClick(networkManager.player);
				} else if (mouseClick == 0) { // right click
					EnderEntity e = Main.getInstance().getEntityById(targetId);
					if (e == null) {
						return;
					}
					if (e.isDead()) {
						return;
					}
					e.onRightClick(networkManager.player);
				}
			}
		});
	}

	public int getTargetId() {
		return targetId;
	}

	public int getMouseClick() {
		return mouseClick;
	}

	public float getTargetX() {
		return targetX;
	}

	public float getTargetY() {
		return targetY;
	}

	public float getTargetZ() {
		return targetZ;
	}
}
