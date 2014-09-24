/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.blocks;

import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class BlockDefinition {

	private final BlockType type;

	public BlockDefinition(BlockType type) {

		this.type = type;
	}

	public BlockType getType() {

		return type;
	}

	public boolean canBreak(Player player, World world, int x, int y, int z) { return true; };

	public int getMaxStackSize() { return 64; };

	public boolean doesInstantBreak() { return false; };

	public String getPlaceSound() { return ""; };
	public String getBreakSound() { return ""; };

	public ItemStack getDrop(Player player, World world, int x, int y, int z) { return new ItemStack(BlockId.valueOf(type.getName().toUpperCase()), (byte) 1, world.getBlock(x, y, z).getData()); };
}
