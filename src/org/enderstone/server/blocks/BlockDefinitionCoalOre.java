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
public class BlockDefinitionCoalOre extends BlockDefinition {

	public BlockDefinitionCoalOre() {

		super(BlockType.COAL_ORE);
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

		return new ItemStack(BlockId.COAL);
	}
}
