/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.commands.vanila;

import org.enderstone.server.Main;
import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.commands.Command;
import static org.enderstone.server.commands.Command.COMMAND_SUCCES;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;

/**
 *
 * @author Fernando
 */
public class StopCommand extends SimpleCommand {

	public StopCommand() {
		super("command.enderstone.stop","stop",CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "halt");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		sender.sendMessage(new SimpleMessage("Stopping server..."));
		Main.getInstance().scheduleShutdown();
		return COMMAND_SUCCES;
	}
}

