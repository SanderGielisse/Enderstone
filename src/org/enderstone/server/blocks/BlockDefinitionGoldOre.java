/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.blocks;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitionGoldOre extends BlockDefinition {

	public BlockDefinitionGoldOre() {

		super(BlockType.GOLD_ORE);
	}

	@Override
	public String getPlaceSound() {

		return "step.stone";
	}

	@Override
	public String getBreakSound() {

		return "dig.stone";
	}
}
