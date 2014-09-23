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
public class BlockDefinitionCobbleStone extends BlockDefinition {

	public BlockDefinitionCobbleStone() {

		super(BlockType.COBBLESTONE);
	}

	@Override
	public String getPlaceSound() {

		return "place.stone";
	}

	@Override
	public String getBreakSound() {

		return "dig.stone";
	}
}
