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
import static org.enderstone.server.commands.Command.COMMAND_SUCCESS;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.regions.EnderWorld;

/**
 *
 * @author Fernando
 */


public class TickRateCommand extends SimpleCommand {

	public TickRateCommand() {
		super("command.enderstone.tickrate","tickrate",CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "speed");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new SimpleMessage("Current tickspeed: "+Main.getInstance().getTickSpeed() + " (default: 20)"));
			return Command.COMMAND_SUCCESS;
		}
                else if (args.length == 1) {
                    String newSpeed = args[0];
                    int speed;
                    try{
                        speed = Integer.parseInt(newSpeed);
                    }catch(NumberFormatException err) {
                        sender.sendMessage(new SimpleMessage("Not a number."));
			return Command.COMMAND_FAILED;
                    }
                    if(speed < 1 || speed > 60) {
                        sender.sendMessage(new SimpleMessage("give a number between 0 and 60 (default: 20"));
                        return Command.COMMAND_FAILED;
                    }
			Main.getInstance().setTickSpeed(speed);
                        return Command.COMMAND_SUCCESS;
		}
                return Command.COMMAND_FAILED;
	}
    
}
