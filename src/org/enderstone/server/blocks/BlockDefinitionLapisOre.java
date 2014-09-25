/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.enderstone.server.blocks;

import java.util.Random;
import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitionLapisOre extends BlockDefinition {

	//TODO drop random amount lapis
	private static final Random random = new Random();

	public BlockDefinitionLapisOre() {
		super(BlockId.LAPIS_ORE);
	}

	@Override
	public String getPlaceSound() {
		return "step.stone";
	}

	@Override
	public String getBreakSound() {
		return "dig.stone";
	}
	
	@Override
	public ItemStack getDrop(Player player, World world, int x, int y, int z) {
		return new ItemStack(BlockId.INK_SACK, (byte) (random.nextInt(7) + 1), (short) 4); //lapis
	}
}
