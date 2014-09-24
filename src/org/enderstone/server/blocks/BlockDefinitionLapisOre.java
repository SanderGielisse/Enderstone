/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.blocks;

import java.util.Random;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitionLapisOre extends BlockDefinition {

	private Random random = new Random();

	public BlockDefinitionLapisOre() {

		super(BlockType.LAPIS_ORE);
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
