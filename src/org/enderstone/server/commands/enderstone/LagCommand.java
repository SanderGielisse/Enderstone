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
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.api.messages.AdvancedMessage.AdvancedMessagePart.HoverEventType;
import org.enderstone.server.api.messages.ChatColor;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import static org.enderstone.server.commands.Command.COMMAND_SUCCES;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;

public class LagCommand extends SimpleCommand {

	public LagCommand() {
		super("command.enderstone.lag", "lag", CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "lagg");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		long[] lastLagg = Main.getInstance().getLastLag();
		long currentTick = Main.getInstance().getCurrentServerTick() - lastLagg.length;
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		long total = 0;
		sender.sendMessage(new SimpleMessage("Current TPS: "));
		for (int i = 0; i < lastLagg.length; i++) {
			long sleepTime = lastLagg[i];
			if(min > sleepTime) min = sleepTime;
			if(max < sleepTime) max = sleepTime;
			total += sleepTime;
			long tick = currentTick + i;
			sender.sendMessage(
					new AdvancedMessage()
						.getBase()
							.setColor(sleepTime < -50 ? ChatColor.RED : sleepTime < 0 ? ChatColor.YELLOW : ChatColor.GREEN)
						.addPart("Tick: " + tick + " ")
						.addPart("Sleeptime: " + sleepTime)
					.build()
			);
		}
		sender.sendMessage(new SimpleMessage("min: " + min + " max: " + max + " avg: " + (total / lastLagg.length)));
		return COMMAND_SUCCES;
	}

}
