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
import java.util.concurrent.atomic.AtomicBoolean;
import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.RegionSet;

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

	private final int endTileId;

	private boolean hasPath;

	private AtomicBoolean checkOnce = new AtomicBoolean();

	public PathFinder(Location startLoc, Location endLoc, int range) {

		Location start = getBlockUnderLocation(startLoc);
		Location end = getBlockUnderLocation(endLoc);

		world = start.getWorld();

		if (world.getBlockIdAt(start).doesInstantBreak()) {

			throw new IllegalArgumentException("Starting location is invalid");
		}

		if (world.getBlockIdAt(end).doesInstantBreak()) {

			throw new IllegalArgumentException("Ending location is invalid");
		}

		startX = start.getBlockX();
		startY = start.getBlockY();
		startZ = start.getBlockZ();

		endX = end.getBlockX();
		endY = end.getBlockY();
		endZ = end.getBlockZ();

		this.range = range;

		PathTile tile = new PathTile(0, 0, 0, null, startX, startY, startZ, endX, endY, endZ);

		openTiles.add(tile);

		processAdjacentTiles(tile);

		int hash = 7;
		hash = 89 * hash + (endX - startX);
		hash = 89 * hash + (endY - startY);
		hash = 89 * hash + (endZ - startZ);

		this.endTileId = hash;
	}

	public Location getStartLocation() {

		return new Location(world, startX, startY, startZ, 0, 0);
	}

	public Location getEndLocation() {

		return new Location(world, endX, endY, endZ, 0, 0);
	}

	public boolean hasPath() {

		return hasPath;
	}

	public List<PathTile> getPath() {

		if (!checkOnce.get()) {

			checkOnce.set(true);

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

		openTiles.remove(drop.hashCode());
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

					int hash = 7;
					hash = 89 * hash + (currentTile.getXOffset() + x);
					hash = 89 * hash + (currentTile.getYOffset() + y);
					hash = 89 * hash + (currentTile.getZOffset() + z);

					if (closedTiles.containsKey(hash)) {
	
						continue;
					}

					if (!(range - Math.abs(currentTile.getXOffset() + x) >= 0 && range - Math.abs(currentTile.getYOffset() + y) >= 0 && range - Math.abs(currentTile.getZOffset() + z) >= 0)) {

						continue;
					}

					if (!canWalkOn(currentTile.getXOffset() + x, currentTile.getYOffset() + y, currentTile.getZOffset() + z)) {

						continue;
					}

					if (x != 0 && z != 0 && (y == 0 || y == 1)) {

						if (!((canWalkOn(currentTile.getXOffset() + x, currentTile.getYOffset() + y, currentTile.getZOffset()) || (canWalkOn(currentTile.getXOffset(), currentTile.getYOffset() + y, currentTile.getZOffset() + z))))) {

							continue;
						}
					}

					PathTile t = new PathTile(currentTile.getXOffset() + x, currentTile.getYOffset() + y, currentTile.getZOffset() + z, currentTile, startX, startY, startZ, endX, endY, endZ);

					possible.add(t);
				}
			}
		}

		for (PathTile t : possible) {

			PathTile open = openTiles.get(t.hashCode());

			if (open == null) {

				openTiles.add(t, false);
			}

			else {

				if ((t.getGoalCost() + t.getHeuristicCost()) < (open.getGoalCost() + open.getHeuristicCost())) {

					open.setParent(currentTile);
				}
			}
		}
	}

	private boolean canWalkOn(PathTile t) {

		return canWalkOn(t.getXOffset(), t.getYOffset(), t.getZOffset());
	}

	//TODO add checks for different sized mobs
	private boolean canWalkOn(int offsetX, int offsetY, int offsetZ) {

		BlockId type = world.getBlockIdAt(startX + offsetX, startY + offsetY, startZ + offsetZ);

		if (!(type.doesInstantBreak() || type == BlockId.LAVA || type == BlockId.LAVA_FLOWING || type == BlockId.FIRE || type == BlockId.CROPS || type == BlockId.LADDER || type == BlockId.FENCE || type == BlockId.FENCE_GATE || type == BlockId.NETHER_FENCE)) {

			BlockId type2 = world.getBlockIdAt(startX + offsetX, startY + offsetY + 1, startZ + offsetZ);
			BlockId type3 = world.getBlockIdAt(startX + offsetX, startY + offsetY + 2, startZ + offsetZ);

			return type2.doesInstantBreak() && type3 == BlockId.AIR;
		}

		return false;
	}

	private Location getBlockUnderLocation(Location loc) {

		Location check = loc.clone();

		for (int i = 0;; i++) {

			if (!loc.getWorld().getBlock(check).getBlock().doesInstantBreak()) {

				return check;
			}

			check.add(0, -1, 0);
		}
	}
}
