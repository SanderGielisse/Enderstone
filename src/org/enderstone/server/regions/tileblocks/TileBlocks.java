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
package org.enderstone.server.regions.tileblocks;

import org.enderstone.server.regions.BlockId;

public class TileBlocks {

	private final static Class<?>[] registeredBlocks = new Class<?>[4096];

	static {
		registeredBlocks[BlockId.WATER.getId()] = WaterTileBlock.class;
		registeredBlocks[BlockId.WATER_FLOWING.getId()] = WaterTileBlock.class;
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends TileBlock> getTileBlock(BlockId blockId) {
		return (Class<? extends TileBlock>) registeredBlocks[blockId.getId()];
	}
}
