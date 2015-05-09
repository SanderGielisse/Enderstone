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
package org.enderstone.server.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.enderstone.server.api.messages.SimpleMessage;

/**
 *
 * @author Ferrybig
 */
public class CommandMap extends Command {

	private final Map<String, Command> commands = new HashMap<>();
	private String[] allCommands = null;
	private final Map<String, Integer> conflictedCommands = new HashMap<>();
	private final static Command COMMAND_CONFLICT = null;
	public final static Integer DEFAULT_ENDERSTONE_COMMAND_PRIORITY = -100;

	/**
	 * Registers a command to this CommandMap.
	 * Conflicted commands will win by the priority of the commands,
	 * if multiple commands have the same priority, then the command won't work
	 * @param cmd The command to register
	 * @return this, you can chain multiple calls to a CommandMap
	 */
	public CommandMap registerCommand(Command... cmd) {
		for (Command c : cmd) {
			int priority = c.getPriority();
			if (registerCommand(c, c.getName(), priority))
				allCommands = null;
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

	/**
	 * Returns the permission used by this Command, a CommandMap returns always null
	 * @return always null
	 */
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
		if (args.length == 0) {
			if (allCommands == null) {
				allCommands = this.commands.keySet().toArray(new String[this.commands.size()]);
				Arrays.sort(allCommands);
			}
			List<String> out = new ArrayList<>();
			for (String commandName : allCommands)
				if (commandName.startsWith(alias))
					out.add("/"+commandName);
			return out;
		}
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
