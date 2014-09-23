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
public class BlockDefinitionDirt extends BlockDefinition {

	public BlockDefinitionDirt() {

		super(BlockType.DIRT);
	}

	@Override
	public String getPlaceSound() {

		return "place.dirt";
	}

	@Override
	public String getBreakSound() {

		return "dig.dirt";
	}

	@Override
	public ItemStack getDrop(Player player, World world, int x, int y, int z) {

		if (world.getBlock(x, y, z).getData() == 2) {

			return new ItemStack(BlockId.DIRT);
		}

		return super.getDrop(player, world, x, y, z);
	}
}
