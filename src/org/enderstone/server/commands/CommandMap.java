package org.enderstone.server.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.enderstone.server.chat.SimpleMessage;

/**
 *
 * @author Ferrybig
 */
public class CommandMap extends Command {

	private final Map<String, Command> commands = new HashMap<>();
	private final Map<String, Integer> conflictedCommands = new HashMap<>();
	private final static Command COMMAND_CONFLICT = null;
	public final static Integer DEFAULT_ENDERSTONE_COMMAND_PRIORITY = -100;

	public CommandMap registerCommand(Command... cmd) {
		for (Command c : cmd) {
			int priority = c.getPriority();
			registerCommand(c, c.getName(), priority);
			for (String alias : c.getAliasses()) registerCommand(c, alias, priority - 1);
		}
		return this;
	}

	private boolean registerCommand(Command cmd, String name, int cmdPriority) {
		if (commands.containsKey(name)) {
			final Command c = commands.get(name);
			final int priority;
			if (cmd.equals(c))
				return true;
			if (c == COMMAND_CONFLICT)
				priority = conflictedCommands.get(name);
			else
				priority = c.getPriority();
			if (cmdPriority > priority) {
				commands.put(name, cmd);
				if (c == COMMAND_CONFLICT)
					conflictedCommands.remove(name);
				return true;
			} else if (cmdPriority == priority)
				if (c != COMMAND_CONFLICT) {
					commands.put(name, COMMAND_CONFLICT);
					conflictedCommands.put(name, priority);
				}
			return false;
		} else
			commands.put(name, cmd);
		return true;
	}

	@Override
	public String getPermision() {
		return null;
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		cmd = this.commands.get(alias);
		if (cmd == null)
			sender.sendMessage(new SimpleMessage("Unknown command " + alias + "!"));
		else
			return cmd.executeCommand(cmd, alias, sender, args);
		return 0;
	}

	@Override
	public List<String> executeTabList(Command cmd, String alias, CommandSender sender, String[] args) {
		cmd = this.commands.get(alias);
		if (cmd == null)
			return Collections.emptyList();
		else
			return cmd.executeTabList(cmd, alias, sender, args);
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public List<String> getAliasses() {
		return Collections.emptyList();
	}

	@Override
	public int getPriority() {
		return Integer.MAX_VALUE;
	}
}
