/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
