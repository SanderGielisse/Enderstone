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

import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitions {

	private static final BlockDefinition[] blocks = new BlockDefinition[4096];

	static {
			blocks[BlockId.AIR.getId()] = new BlockDefinition(BlockId.AIR);
			blocks[BlockId.STONE.getId()] = new BlockDefinitionStone();
			blocks[BlockId.GRASS.getId()] = new BlockDefinitionGrass();
			blocks[BlockId.DIRT.getId()] = new BlockDefinitionDirt();
			blocks[BlockId.COBBLESTONE.getId()] = new BlockDefinitionCobbleStone();
			blocks[BlockId.BEDROCK.getId()] = new BlockDefinitionBedrock();
			blocks[BlockId.COAL_ORE.getId()] = new BlockDefinitionCoalOre();
			blocks[BlockId.GLASS.getId()] = new BlockDefinitionGlass();
			blocks[BlockId.GOLD_ORE.getId()] = new BlockDefinitionGoldOre();
			blocks[BlockId.IRON_ORE.getId()] = new BlockDefinitionIronOre();
			blocks[BlockId.GRAVEL.getId()] = new BlockDefinitionGravel();
			blocks[BlockId.LAPIS_ORE.getId()] = new BlockDefinitionLapisOre();
			blocks[BlockId.LAPIS_BLOCK.getId()] = new BlockDefinitionLapisBlock();
			blocks[BlockId.LEAVES.getId()] = new BlockDefinitionLeaf();
			blocks[BlockId.LOG.getId()] = new BlockDefinitionLog();
			blocks[BlockId.SAND.getId()] = new BlockDefinitionSand();
			blocks[BlockId.SAPLING.getId()] = new BlockDefinitionSapling();
			blocks[BlockId.SPONGE.getId()] = new BlockDefinitionSponge();
			blocks[BlockId.WOOD.getId()] = new BlockDefinitionWood();
	};

	public static BlockDefinition getBlock(BlockId type) {
		BlockDefinition definition = blocks[type.getId()];
		if (definition != null) {
			return definition;
		} else {
			blocks[type.getId()] =  new BlockDefinition(type);
			return blocks[type.getId()];
		}
	}
}
