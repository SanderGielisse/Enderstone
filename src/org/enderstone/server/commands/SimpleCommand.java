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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Fernando
 */
public abstract class SimpleCommand extends Command{

	protected final String permissions;
	protected final String name;
	protected final List<String> aliasses;
	protected final int priority;

	public SimpleCommand(String permissions, String name, int priority, String ... aliasses) {
		this.permissions = permissions;
		this.name = name;
		this.aliasses = Collections.unmodifiableList(Arrays.asList(aliasses));
		this.priority = priority;
	}
	
	@Override
	public String getPermision() {
		return this.permissions;
	}

	@Override
	public abstract int executeCommand(Command cmd, String alias, CommandSender sender, String[] args);

	@Override
	public List<String> executeTabList(Command cmd, String alias, CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getAliasses() {
		return aliasses;
	}

	@Override
	public int getPriority() {
		return priority;
	}
	
}
