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

package org.enderstone.server.blocks;

import java.util.HashMap;
import java.util.Map;
import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitions {

	private static final Map<Integer, BlockDefinition> blocks = new HashMap<>();

	static {{

		blocks.put(0, new BlockDefinition(BlockType.AIR));
		blocks.put(1, new BlockDefinitionStone());
		blocks.put(2, new BlockDefinitionGrass());
		blocks.put(3, new BlockDefinitionDirt());
		blocks.put(4, new BlockDefinitionCobbleStone());
	}};

	@Deprecated
	public static BlockDefinition getBlock(BlockId id) {

		return getBlock(BlockType.valueOf(id.getName().toUpperCase()));
	}

	public static BlockDefinition getBlock(BlockType type) {

		BlockDefinition definition = blocks.get(type.getId());

		if (definition != null) {

			return definition;
		}

		else {

			return new BlockDefinition(type);
		}
	}
}
