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
import java.util.Iterator;

import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;
import org.enderstone.server.regions.BlockId;

public class PacketInAnimation extends Packet {

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x0A;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		// arm animation
		Main.getInstance().sendToMainThread(new Runnable() {

			private final byte armSwingId = 0;

			@Override
			public void run() {
				Iterator<Location> lineOfSight = networkManager.player.getLineOfSight(4, 3);
				while(lineOfSight.hasNext()){
					Location next = lineOfSight.next();
					if(networkManager.player.getWorld().getBlock(next).getBlock() == BlockId.FIRE){
						networkManager.player.getWorld().setBlockAt(next.getBlockX(), next.getBlockY(), next.getBlockZ(), BlockId.AIR, (byte) 0);
						break;
					}
				}
				
				networkManager.player.getWorld().broadcastPacket(new PacketOutAnimation(networkManager.player.getEntityId(), armSwingId), networkManager.player.getLocation(), networkManager.player);
			}
		});
	}
}
