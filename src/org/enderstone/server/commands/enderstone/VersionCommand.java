/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.commands.enderstone;

import java.util.Arrays;
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
public class VersionCommand extends SimpleCommand{
	public VersionCommand() {
		super("command.enderstone.version","version",CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY, "ver");
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		sender.sendMessage(new SimpleMessage(Main.NAME + " " + Main.PROTOCOL_VERSION + " Created by: "+ Arrays.asList(Main.AUTHORS)));
		return COMMAND_SUCCES;
	}
	
}
