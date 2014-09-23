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
public class BlockDefinitionSand extends BlockDefinition {

	public BlockDefinitionSand() {

		super(BlockType.SAND);
	}

	@Override
	public String getPlaceSound() {

		return "place.sand";
	}

	@Override
	public String getBreakSound() {

		return "dig.sand";
	}
}
