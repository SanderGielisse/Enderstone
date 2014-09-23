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
public class BlockDefinitionLapisBlock extends BlockDefinition {

	public BlockDefinitionLapisBlock() {

		super(BlockType.LAPIS_BLOCK);
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
