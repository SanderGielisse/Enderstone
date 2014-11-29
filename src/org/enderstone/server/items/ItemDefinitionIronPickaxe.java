/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.items;

import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class ItemDefinitionIronPickaxe extends ItemDefinition {

	public ItemDefinitionIronPickaxe() {
		super(BlockId.IRON_PICKAXE);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getAttackDamage() {
		return 5;
	}
}
