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
package org.enderstone.server.commands.vanila;

import java.util.List;
import org.enderstone.server.Main;
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.api.messages.AdvancedMessage.AdvancedMessagePart.ClickEventType;
import org.enderstone.server.api.messages.AdvancedMessage.AdvancedMessagePart.HoverEventType;
import org.enderstone.server.api.messages.ChatColor;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.EnderPlayer;

/**
 *
 * @author Fernando
 */
public class TellCommand extends SimpleCommand {

	public TellCommand() {
		super("command.enderstone.tell","tell",CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "msg", "w", "pm");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		if (args.length > 1) {
			EnderPlayer target = Main.getInstance().getPlayer(args[0]);
			if(target == null)
			{
				sender.sendMessage(new SimpleMessage("TargetPlayer cannot be found!"));
				return COMMAND_FAILED;
			}
			if(target == sender)
			{
				sender.sendMessage(new SimpleMessage("If you send a message to yourself, it is better to keep it private"));
				return COMMAND_FAILED;
			}
			StringBuilder message = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				message.append(args[i]).append(' ');
			}
			message.setLength(message.length() - 1);
			AdvancedMessage m = new AdvancedMessage()
					.addPart("<")
						.setColor(ChatColor.WHITE)
						.setClickEvent(ClickEventType.SUGGEST_COMMAND, "/pm "+sender.getName()+" ")
					.addPart(sender.getName())
						.setHoverEvent(HoverEventType.SHOW_TEXT, sender.toString())
						.setColor(ChatColor.GRAY)
					.addPart(" --> ")
					.addPart(target.getName())
						.setHoverEvent(HoverEventType.SHOW_TEXT, target.toString())
						.setColor(ChatColor.GRAY)
					.addPart("> ")
					.addPart(message.toString())
					.build();
			target.sendMessage(m);
			sender.sendMessage(m);
			return COMMAND_SUCCES; // 1 person recieved the message
		}
		sender.sendMessage(new SimpleMessage("Usage: /tell <person> <command>"));
		return COMMAND_FAILED;
	}

	@Override
	public List<String> executeTabList(Command cmd, String alias, CommandSender sender, String[] args) {
		if (args.length == 0)
			return calculateMissingArgumentsPlayer("", sender);
		return calculateMissingArgumentsPlayer(args[args.length - 1], sender);
	}

}
