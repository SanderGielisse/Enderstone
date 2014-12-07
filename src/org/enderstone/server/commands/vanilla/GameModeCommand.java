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
package org.enderstone.server.commands.vanilla;

import org.enderstone.server.Main;
import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.api.messages.ChatColor;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import static org.enderstone.server.commands.Command.COMMAND_FAILED;
import static org.enderstone.server.commands.Command.COMMAND_SUCCESS;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.player.EnderPlayer;

/**
 *
 * @author gyroninja
 */
public class GameModeCommand extends SimpleCommand {

	public GameModeCommand() {
		super("command.enderstone.gamemode", "gamemode", CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "gm");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {

		if (args.length > 0) {
			EnderPlayer target = null;
			if (args.length > 1) {
				target = Main.getInstance().getPlayer(args[1]);
			}
			else if (sender instanceof EnderPlayer) {
				target = (EnderPlayer) sender;
			}
			if(target == null)
			{
				sender.sendMessage(new SimpleMessage("Target player cannot be found!"));
				return COMMAND_FAILED;
			}
			GameMode mode;
			try {
				mode = GameMode.values()[Integer.valueOf(args[0])];
			}
			catch (IllegalArgumentException ex) {
				if (ex instanceof NumberFormatException) {
					try {
						mode = GameMode.valueOf(args[0]);
					}
					catch (IllegalArgumentException ex2) {
						sender.sendMessage(new SimpleMessage(args[0] + " is not a valid gamemode!"));
						return COMMAND_FAILED;
					}
				}
				else {
					sender.sendMessage(new SimpleMessage("Gamemode " + args[0] + " is not a valid gamemode!"));
					return COMMAND_FAILED;
				}
			}
			if (mode == null) {
				return COMMAND_FAILED;
			}
			target.setGameMode(mode);
			target.sendMessage(new SimpleMessage("Your gamemode has been set to " + mode.toString().toLowerCase() + "!"));
			if (target != sender) {
				sender.sendMessage(new SimpleMessage(args[0] + "'s gamemode has been set to " + mode.toString().toLowerCase() + "!"));
			}
			return COMMAND_SUCCESS;
		}
		sender.sendMessage(new SimpleMessage("Usage: /gamemode <mode> [target]"));
		return COMMAND_FAILED;
	}
}
