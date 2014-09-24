/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.blocks;

import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Player;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitionBedrock extends BlockDefinition {

	public BlockDefinitionBedrock() {

		super(BlockType.BEDROCK);
	}

	@Override
	public boolean canBreak(Player player, World w, int x, int y, int z) {

		return player.getGameMode() == GameMode.CREATIVE;
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
