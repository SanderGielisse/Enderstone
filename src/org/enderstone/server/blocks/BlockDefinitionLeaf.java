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
public class BlockDefinitionLeaf extends BlockDefinition {

	public BlockDefinitionLeaf() {

		super(BlockType.LEAVES);
	}

	@Override
	public String getPlaceSound() {

		return "place.leaf";
	}

	@Override
	public String getBreakSound() {

		return "dig.leaf";
	}
}
