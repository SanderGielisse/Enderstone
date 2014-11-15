/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.items;

import org.enderstone.server.api.Block;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class ItemDefinitionFlintAndSteel extends ItemDefinition {

	public ItemDefinitionFlintAndSteel() {

		super(BlockId.FLINT_AND_STEEL);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void onRightClick(EnderPlayer player, Block block) {
System.out.println(block.getBlock());
		if (block.getBlock() == BlockId.AIR) {
			block.setBlock(BlockId.FIRE, (byte) 0);
		}
	}
}
