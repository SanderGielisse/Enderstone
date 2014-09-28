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
package org.enderstone.server.commands.enderstone;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.EntitySpider;
import org.enderstone.server.entity.pathfinding.PathFinder;
import org.enderstone.server.entity.pathfinding.PathTile;

/**
 *
 * @author gyroninja
 */
public class AiCommand extends SimpleCommand {

	public AiCommand() {
		super("command.enderstone.ai", "ai", CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY);
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {

		if (sender instanceof EnderPlayer) {

			EnderPlayer player = (EnderPlayer) sender;

			final Location start = player.getLocation().clone().add(0, -1, 0);

			PathFinder pathfinder = new PathFinder(start, new Location(player.getWorld(), 0, 63, 0, 0, 0), 32);

			final List<PathTile> path = pathfinder.getPath();

			final EntitySpider aispider = new EntitySpider(player.getWorld(), path.get(0).getLocation(start).clone().add(0, 1, 0));

			player.getWorld().addEntity(aispider);

			Runnable moveTask = new Runnable() {

				@Override
				public void run() {

					try {
						for (int i = 1; i < path.size(); i++) {

							final int node = i; 

							Main.getInstance().sendToMainThread(new Runnable() {

								@Override
								public void run() {

									aispider.teleport(path.get(node).getLocation(start).clone().add(0, 1, 0));
								}
							});
						}

						synchronized (this) {

							wait(500);
						}
					}

					catch(InterruptedException ex) {

						System.out.println("Move thread interupted");
					}
				}
			};

			Thread moveThread = new Thread(moveTask);

			moveThread.start();
		}

		return COMMAND_SUCCES;
	}
}
