
package org.enderstone.server.commands.enderstone;

import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.EnderPlayer;

/**
 *
 * @author Fernando
 */
public class QuitCommand extends SimpleCommand{
	public QuitCommand() {
		super("command.enderstone.quit","quit",CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY);
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		((EnderPlayer)sender).networkManager.disconnect("Goodbye");
		return COMMAND_SUCCES;
	}
	
}