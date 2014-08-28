/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.commands.enderstone;

import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;

/**
 *
 * @author Fernando
 */
public class PingCommand extends SimpleCommand{
	public PingCommand() {
		super("command.enderstone.ping","ping",CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "pong", "hello", "backdoor");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		sender.sendMessage(new SimpleMessage("Ping! Pong!"));
		return COMMAND_SUCCES;
	}
	
}
