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
public class BlockDefinitionStone extends BlockDefinition {

	public BlockDefinitionStone() {

		super(BlockType.STONE);
	}

	@Override
	public String getPlaceSound() {

		return "step.stone";
	}

	@Override
	public String getBreakSound() {

		return "dig.stone";
	}

	@Override
	public ItemStack getDrop(Player player, World world, int x, int y, int z) {

		if (world.getBlock(x, y, z).getData() == 0) {

			return new ItemStack(BlockId.COBBLESTONE);
		}

		else {

			return super.getDrop(player, world, x, y, z);
		}
	}
}
