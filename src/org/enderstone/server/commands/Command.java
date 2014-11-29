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
import java.util.Collection;
import java.util.List;

import org.enderstone.server.Main;
import org.enderstone.server.entity.player.EnderPlayer;

/**
 *
 * @author Ferrybig
 */
public abstract class Command {

	public static final int COMMAND_SUCCES = 1;
	public static final int COMMAND_FAILED = 0;

	public abstract String getPermision();

	public abstract int executeCommand(Command cmd, String alias, CommandSender sender, String[] args);

	public abstract List<String> executeTabList(Command cmd, String alias, CommandSender sender, String[] args);

	public abstract String getName();

	/**
	 * Get the command aliasses
	 *
	 * @return
	 */
	public abstract List<String> getAliasses();

	/**
	 * The command priority, higher priorities means the more likely the command is to be included in the resulting
	 * commandmap
	 *
	 * @return The command priority
	 */
	public abstract int getPriority();

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Command other = (Command) obj;
		if (this.getName() == null ? other.getName() != null : !getName().equals(other.getName()))
			return false;
		return this.getAliasses().equals(other.getAliasses());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash *= getName().hashCode();
		hash *= getAliasses().hashCode();
		return hash;
	}

	public static final List<String> calculateMissingArgumentsPlayer(String playerName, CommandSender executer)
	{
		return calculateMissingArgumentsPlayer(playerName, executer instanceof EnderPlayer ? (EnderPlayer)executer : null);
	}
	public static final List<String> calculateMissingArgumentsPlayer(String playerName, EnderPlayer executer) {
		Collection<? extends EnderPlayer> players = Main.getInstance().onlinePlayers;

		List<String> found = new ArrayList<>(players.size());
		String lowerName = playerName.toLowerCase();
		for (EnderPlayer player : players) {
			if (executer != null)
				if (!executer.canSee(player))
					continue;
			if (player.getName().toLowerCase().startsWith(lowerName)) {
				found.add(player.getName());
			}
		}
		return found;
	}
}
