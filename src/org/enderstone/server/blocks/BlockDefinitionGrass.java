/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.blocks;

import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitionGrass extends BlockDefinition {

	public BlockDefinitionGrass() {

		super(BlockType.GRASS);
	}

	@Override
	public String getPlaceSound() {

		return "place.grass";
	}

	@Override
	public String getBreakSound() {

		return "dig.grass";
	}

	@Override
	public ItemStack getDrop(Player player, World world, int x, int y, int z) {

		return new ItemStack(BlockId.DIRT);
	}
}
