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
public class BlockDefinitionGravel extends BlockDefinition {

	public BlockDefinitionGravel() {

		super(BlockType.GRAVEL);
	}

	@Override
	public String getPlaceSound() {

		return "step.gravel";
	}

	@Override
	public String getBreakSound() {

		return "dig.gravel";
	}
}
