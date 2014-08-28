package org.enderstone.server.commands;

import java.util.List;

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
	 * @return 
	 */
	public abstract List<String> getAliasses();
	
	/**
	 * The command priority, higher priorities means the more likely the
	 *   command is to be included in the resulting commandmap
	 * @return The command priority
	 */
	public abstract int getPriority();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Command other = (Command) obj;
		if(this.getName() == null ? other.getName() != null : !getName().equals(other.getName()))
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
}
