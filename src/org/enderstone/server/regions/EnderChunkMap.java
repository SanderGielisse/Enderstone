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
package org.enderstone.server.regions;

import org.enderstone.server.EnderLogger;
import org.enderstone.server.packet.play.PacketOutChunkData;

/**
 *
 * @author Fernando
 */
public class EnderChunkMap {

	public byte[] chunkData;
	public int primaryBitmap;

	public PacketOutChunkData toPacket(int x, int z) {
		
		EnderLogger.warn("SIZE: " + chunkData.length);
		
		return new PacketOutChunkData(x, z, true, (short) (primaryBitmap & '\uffff'), chunkData.length, chunkData);
	}
}
