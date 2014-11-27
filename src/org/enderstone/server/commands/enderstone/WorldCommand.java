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
package org.enderstone.server.commands.enderstone;

import org.enderstone.server.Main;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.regions.EnderWorld;

public class WorldCommand extends SimpleCommand {

	public WorldCommand() {
		super("command.enderstone.world", "world", CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "goto");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(new SimpleMessage("Correct usage: /world <worldname>"));
			return Command.COMMAND_FAILED;
		}
		String worldName = args[0];
		EnderWorld world = getWorld(worldName);

		if (world == null) {
			sender.sendMessage(new SimpleMessage("World " + worldName + " cannot be found."));
			return Command.COMMAND_FAILED;
		}

		if (!(sender instanceof EnderPlayer)) {
			sender.sendMessage(new SimpleMessage("This command can only be excecuted by players!"));
			return Command.COMMAND_FAILED;
		}
		((EnderPlayer) sender).switchWorld(world);
		sender.sendMessage(new SimpleMessage("You succesfully switched to world " + worldName + "."));
		return Command.COMMAND_SUCCES;
	}

	private EnderWorld getWorld(String name) {
		for (EnderWorld world : Main.getInstance().worlds) {
			if (world.worldName.equals(name)) {
				return world;
			}
		}
		return null;
	}

}
