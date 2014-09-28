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
package org.enderstone.server.entity.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.enderstone.server.api.Location;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderWorld;

/**
 *
 * @author gyroninja
 */
public class PathFinder {

	private EnderWorld world;

	private final int startX;
	private final int startY;
	private final int startZ;

	private final int endX;
	private final int endY;
	private final int endZ;

	private final int range;

	private final PathTileList openTiles = new PathTileList();
	private final PathTileList closedTiles = new PathTileList();

	private final String endTileId;

	private boolean hasPath;

	private boolean checkOnce;

	public PathFinder(Location start, Location end, int range) {

		if (start.getWorld().getBlockIdAt(start).doesInstantBreak()) {

			throw new IllegalArgumentException("Starting location is invalid");
		}

		if (end.getWorld().getBlockIdAt(end).doesInstantBreak()) {

			throw new IllegalArgumentException("Ending location is invalid");
		}

		world = start.getWorld();

		startX = start.getBlockX();
		startY = start.getBlockY();
		startZ = start.getBlockZ();

		endX = end.getBlockX();
		endY = end.getBlockY();
		endZ = end.getBlockZ();

		this.range = range;

		PathTile tile = new PathTile(0, 0, 0, null);

		tile.calculateCost(startX, startY, startZ, endX, endY, endZ);

		openTiles.add(tile);

		processAdjacentTiles(tile);

		this.endTileId = "PathTile{xOffset=" + (endX - startX) + " yOffset=" + (endY - startY) + " zOffset=" + (endZ - startZ) + '}';
	}

	public Location getEndLocation() {

		return new Location(world, endX, endY, endZ, 0, 0);
	}

	public boolean hasPath() {

		return hasPath;
	}

	public ArrayList<PathTile> getPath() {

		if (!checkOnce) {

			checkOnce ^= true;

			if (abs(startX - endX) > range || abs(startY - endY) > range || abs(startZ - endZ) > range) {

				hasPath = false;

				return null;
			}

			PathTile currentTile = null;

			while (canContinue()) {

				currentTile = getLowestCostTile();

				processAdjacentTiles(currentTile);
			}

			if (!hasPath) {

				return null;
			}

			else {

				List<PathTile> pathTrace = new LinkedList<>();

				PathTile parentTile;

				pathTrace.add(currentTile);

				while ((parentTile = currentTile.getParent()) != null) {

					pathTrace.add(parentTile);

					currentTile = parentTile;
				}

				Collections.reverse(pathTrace);

				return new ArrayList<>(pathTrace);
			}
		}

		return null;
	}

	private int abs(int x) {

		return x > 0 ? x : -x;
	}

	private boolean canContinue() {

		if (openTiles.isEmpty()) {

			hasPath = false;

			return false;
		}

		if (closedTiles.containsKey(endTileId)) {

			hasPath = true;

			return false;
		}

		else {

			return true;
		}
	}

	private PathTile getLowestCostTile() {

		double cost = 0;

		PathTile drop = null;

		for (PathTile t : openTiles.values()) {

			t.calculateCost(startX, startY, startZ, endX, endY, endZ);

			if (cost == 0) {

				cost = t.getGoalCost() + t.getHeuristicCost();

				drop = t;
			}

			else {

				double tileCost = t.getGoalCost() + t.getHeuristicCost();

				if (tileCost < cost) {

					cost = tileCost;

					drop = t;
				}
			}
		}

		openTiles.remove(drop.toString());
		closedTiles.add(drop);

		return drop;
	}

	private void processAdjacentTiles(PathTile currentTile) {

		Set<PathTile> possible = new HashSet<>(26);//3x3x3 region minus start tile;

		for (int x = -1; x <= 1; x++) {

			for (int y = -1; y <= 1; y++) {

				for (int z = -1; z <= 1; z++) {

					if (x == 0 && y == 0 && z == 0) {

						continue;
					}

					PathTile t = new PathTile(currentTile.getXOffset() + x, currentTile.getYOffset() + y, currentTile.getZOffset() + z, currentTile);

					if (!t.isInRange(range)) {

						continue;
					}

					if (!canWalkOn(t)) {

						continue;
					}

					if (closedTiles.containsKey(t.toString())) {
	
						continue;
					}

					if (x != 0 && z != 0 && (y == 0 || y == 1)) {

						PathTile xOffset = new PathTile(currentTile.getXOffset() + x, currentTile.getYOffset() + y, currentTile.getZOffset(), currentTile);
						PathTile zOffset = new PathTile(currentTile.getXOffset(), currentTile.getYOffset() + y, currentTile.getZOffset() + z, currentTile);

						if (!((canWalkOn(xOffset) || (canWalkOn(zOffset))))) {

							continue;
						}
					}
	
					t.calculateCost(startX, startY, startZ, endX, endY, endZ);

					possible.add(t);
				}
			}
		}

		for (PathTile t : possible) {

			PathTile open = openTiles.get(t.toString());

			if (open == null) {

				openTiles.add(t, false);
			}

			else {

				if ((t.getGoalCost() + t.getHeuristicCost()) < (open.getGoalCost() + open.getHeuristicCost())) {

					open.setParent(currentTile);

					t.calculateCost(startX, startY, startZ, endX, endY, endZ);
				}
			}
		}
	}

	//TODO add checks for different sized mobs
	private boolean canWalkOn(PathTile t) {

		BlockId type = world.getBlockIdAt(startX + t.getXOffset(), startY + t.getYOffset(), startZ + t.getZOffset());

		if (!(type.doesInstantBreak() || type == BlockId.LAVA || type == BlockId.LAVA_FLOWING || type == BlockId.FIRE || type == BlockId.CROPS || type == BlockId.LADDER || type == BlockId.FENCE || type == BlockId.FENCE_GATE || type == BlockId.NETHER_FENCE)) {

			BlockId type2 = world.getBlockIdAt(startX + t.getXOffset(), startY + t.getYOffset() + 1, startZ + t.getZOffset());
			BlockId type3 = world.getBlockIdAt(startX + t.getXOffset(), startY + t.getYOffset() + 2, startZ + t.getZOffset());

			return type2.doesInstantBreak() && type3 == BlockId.AIR;
		}

		return false;
	}
}
