/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

		return blocks.get((int) id.getId());
	}

	public static BlockDefinition getBlock(BlockType type) {

		return blocks.get(type.getId());
	}
}
