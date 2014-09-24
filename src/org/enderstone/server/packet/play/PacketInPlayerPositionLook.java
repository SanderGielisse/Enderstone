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
import org.enderstone.server.api.Location;
import org.enderstone.server.api.event.player.PlayerMoveEvent;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketInPlayerPositionLook extends Packet {

	private double x;
	private double feetY;
	private double z;
	private float yaw;
	private float pitch;
	private boolean onGround;
	
	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.x = wrapper.readDouble();
		this.feetY = wrapper.readDouble();
		this.z = wrapper.readDouble();
		this.yaw = wrapper.readFloat();
		this.pitch = wrapper.readFloat();
		this.onGround = wrapper.readBoolean();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return (getDoubleSize() * 3) + (getFloatSize() * 2) + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x06;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				Location loc = networkManager.player.getLocation();
				if (networkManager.player.waitingForValidMoveAfterTeleport > 0) {
					if (Math.max(Math.max(
							getX() < loc.getX() ? loc.getX() - getX() : getX() - loc.getX(),
							getFeetY() < loc.getY() ? loc.getY() - getFeetY() : getFeetY() - loc.getY()),
							getZ() < loc.getZ() ? loc.getZ() - getZ() : getZ() - loc.getZ())> 0.1) {
						if (networkManager.player.waitingForValidMoveAfterTeleport++ > 100) {
							networkManager.player.teleportInternally(loc);
						}
						return;
					}
					networkManager.player.waitingForValidMoveAfterTeleport = 0;
				}
				Location newLoc = loc.clone();
				newLoc.setX(getX());
				newLoc.setY(getFeetY());
				newLoc.setZ(getZ());
				newLoc.setPitch(getPitch());
				newLoc.setYaw(getYaw());
				if(Main.getInstance().callEvent(new PlayerMoveEvent(networkManager.player, loc, newLoc))){
					return;
				}
				
				networkManager.player.broadcastLocation(new Location(networkManager.player.getWorld(), getX(), getFeetY(), getZ(), getYaw(), getPitch()));
				networkManager.player.broadcastRotation(getPitch(), getYaw());
				loc.setX(getX());
				loc.setY(getFeetY());
				loc.setZ(getZ());
				loc.setPitch(getPitch());
				loc.setYaw(getYaw());
				networkManager.player.setOnGround(isOnGround());
			}
		});
	}

	public double getX() {
		return x;
	}

	public double getFeetY() {
		return feetY;
	}

	public double getHeadY() {
		return getFeetY() + 1.62;
	}

	public double getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public boolean isOnGround() {
		return onGround;
	}
}
