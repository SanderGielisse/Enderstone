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
public class BlockDefinitionGlass extends BlockDefinition {

	public BlockDefinitionGlass() {

		super(BlockType.GLASS);
	}

	@Override
	public String getPlaceSound() {

		return "place.glass";
	}

	@Override
	public String getBreakSound() {

		return "dig.glass";
	}
}
