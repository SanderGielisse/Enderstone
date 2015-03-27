package org.enderstone.server.commands.enderstone;

import java.util.ArrayList;

import org.enderstone.server.api.Location;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.commands.Command;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.CommandSender;
import org.enderstone.server.commands.SimpleCommand;
import org.enderstone.server.entity.pathfinding.TargetPathfinder;
import org.enderstone.server.entity.pathfinding.Tile;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.regions.BlockId;

public class AiCommand extends SimpleCommand {

	public AiCommand() {
		super("command.enderstone.debug.ai", "ai", CommandMap.DEFAULT_ENDERSTONE_COMMAND_PRIORITY);
	}

	@Override
	public int executeCommand(Command cmd, String alias, CommandSender sender, String[] args) {
		EnderPlayer p = ((EnderPlayer) sender);

		long startTime = System.currentTimeMillis();

		Location start = p.getLocation().getNearestEntity(32).getLocation().add(0, -1, 0), end = p.getLocation().add(0, -1, 0);
		int range = 32;

		TargetPathfinder pathFinder = new TargetPathfinder(start, end, range);
		ArrayList<Tile> tiles = pathFinder.iterate();
		if (pathFinder.didSucceed()) {
			for (Tile t : tiles) {
				t.getLocation(start).getBlock().setBlock(BlockId.DIAMOND_BLOCK, (byte) 0);
			}
			long endTime = System.currentTimeMillis();
			p.sendMessage(new SimpleMessage("Generating path took: " + (endTime - startTime)));
		}

		return COMMAND_SUCCESS;
	}
}
