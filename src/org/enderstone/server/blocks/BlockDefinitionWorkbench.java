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

import org.enderstone.server.api.Block;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.inventory.CraftingInventory;
import org.enderstone.server.regions.BlockId;

public class BlockDefinitionWorkbench extends BlockDefinition {

	public BlockDefinitionWorkbench() {
		super(BlockId.WORKBENCH);
	}

	@Override
	public String getPlaceSound() {
		return "step.wood";
	}

	@Override
	public String getBreakSound() {
		return "dig.wood";
	}
	
	@Override
	public boolean onRightClick(EnderPlayer player, Block block) {
		player.getInventoryHandler().openInventory(new CraftingInventory(player.getLocation()));
		return true;
	}
}
