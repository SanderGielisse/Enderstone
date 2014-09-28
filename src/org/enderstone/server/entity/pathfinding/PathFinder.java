/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.entity.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.World;
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

	private int result;

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

		tile.calculatePastCost(startX, startY, startZ, true);
		tile.calculateFutureCost(startX, startY, startZ, endX, endY, endZ, true);

		openTiles.add(tile);

		processAdjacentTiles(tile);

		this.endTileId = "PathTile{xOffset=" + (endX - startX) + " yOffset=" + (endY - startY) + " zOffset=" + (endZ - startZ) + '}';
	}

	public Location getEndLocation() {

		return new Location(world, endX, endY, endZ, 0, 0);
	}

	public int getResult() {

		return result;
	}

	public ArrayList<PathTile> iterate() {

		if (!checkOnce) {

			checkOnce ^= true;

			if (abs(startX - endX) > range || abs(startY - endY) > range || abs(startZ - endZ) > range) {

				result = -1;

				return null;
			}

			PathTile currentTile = null;

			while (canContinue()) {

				currentTile = getLowestCostTile();

				processAdjacentTiles(currentTile);
			}

			if (this.result == -1) {

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

			result = -1;

			return false;
		}

		if (closedTiles.containsKey(endTileId)) {

			this.result = 0;

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

			t.calculatePastCost(startX, startY, startZ, true);
			t.calculateFutureCost(startX, startY, startZ, endX, endY, endZ, true);

			double tileCost = t.getPastCost() + t.getFutureCost();

			if (cost == 0) {

				drop = t;
			}

			else {

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
	
					t.calculatePastCost(startX, startY, startZ, true);
					t.calculateFutureCost(startX, startY, startZ, endX, endY, endZ, true);

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

				if ((t.getPastCost() + t.getFutureCost()) < (open.getPastCost() + open.getFutureCost())) {

					open.setParent(currentTile);

					open.calculatePastCost(startX, startY, startZ, true);
					open.calculateFutureCost(startX, startY, startZ, endX, endY, endZ, true);
				}
			}
		}
	}

	//TODO add checks for two high mobs right now supports 1x1x1 mobs and support for open fence gates
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
