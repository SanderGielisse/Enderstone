package org.enderstone.server.commands.vanila;

import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.commands.Command;
import static org.enderstone.server.commands.Command.COMMAND_SUCCES;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.EnderPlayer;

/**
 * Simple /kill command
 * @author ferrybig
 */
public class KillCommand extends SimpleCommand {

	public KillCommand() {
		super("command.enderstone.kill", "kill", CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY);
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		if (sender instanceof EnderPlayer) {
			sender.sendMessage(new SimpleMessage("Killing: "+sender.getName()));
			((EnderPlayer)sender).kill();
		}
		else
		{
			sender.sendMessage(new SimpleMessage("Cannot kill console!"));
		}
		return COMMAND_SUCCES;
	}
}
