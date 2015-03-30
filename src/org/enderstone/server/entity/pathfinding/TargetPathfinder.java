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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.enderstone.server.api.Block;
import org.enderstone.server.api.Location;
import org.enderstone.server.regions.EnderWorld;

public class TargetPathfinder {

	/**
	 * 
	 * See link below for more information about how this works
	 * http://www.policyalmanac.org/games/aStarTutorial.htm
	 * 
	 * Thanks to @Adamki11s, who's source I took inspiration from on how to do this, check out his Bukkit post here:
	 * https://bukkit.org/threads/lib-a-pathfinding-algorithm.129786/
	 */
	
	private final int startX;
	private final int startY;
	private final int startZ;
	private final int endX;
	private final int endY;
	private final int endZ;
	private final EnderWorld world;

	private boolean succes;

	private final Map<String, Tile> todo = new HashMap<String, Tile>();
	private final Map<String, Tile> done = new HashMap<String, Tile>();

	private final int range;
	private Tile targetTile;

	public TargetPathfinder(Location start, Location end, int range) {
		this.world = start.getWorld();
		this.startX = start.getBlockX();
		this.startY = start.getBlockY();
		this.startZ = start.getBlockZ();
		this.endX = end.getBlockX();
		this.endY = end.getBlockY();
		this.endZ = end.getBlockZ();
		this.range = range;

		if (!(this.isWalkable(start)) || !(this.isWalkable(end))) {
			this.succes = false;
			return;
		}

		short sh = 0;
		Tile t = new Tile(sh, sh, sh, null);
		t.calculateBoth(startX, startY, startZ, endX, endY, endZ);
		this.todo.put(t.getID(), t);
		this.processAdjacentTiles(t);

		this.targetTile = new Tile(((short) (endX - startX)), ((short) (endY - startY)), ((short) (endZ - startZ)), null);
	}

	public boolean didSucceed() {
		return this.succes;
	}
	
	/**
	 * NEVER call this method more than once
	 * 
	 */
	public ArrayList<Tile> iterate() {
		if ((EuclideanUtil.absolute(startX - endX) > range) || (EuclideanUtil.absolute(startY - endY) > range) || (EuclideanUtil.absolute(startZ - endZ) > range)) {
			this.succes = false;
			return null;
		}

		Tile current = null;
		while (this.shouldContinue()) {
			// get the one with lowest F costs on open list
			current = this.getLowestFTile();
			// process tiles
			this.processAdjacentTiles(current);
		}

		if (!this.didSucceed()) { // didn't succeed
			return null;
		} else {
			// path found
			LinkedList<Tile> foundRoute = new LinkedList<Tile>();
			foundRoute.add(current);
			
			Tile parent = null;
			while ((parent = current.getParent()) != null) {
				foundRoute.add(parent);
				current = parent;
			}
			Collections.reverse(foundRoute); // from (endLoc -> startLoc) to (startLoc -> endLoc)
			return new ArrayList<Tile>(foundRoute);
		}
	}

	private boolean shouldContinue() {
		if (todo.size() == 0) {
			this.succes = false;
			return false;
		} else {
			if (done.containsKey(this.targetTile.getID())) {
				// we've reached our destination
				this.succes = true;
				return false;
			} else {
				// keep searching
				return true;
			}
		}
	}

	private Tile getLowestFTile() {
		double f = 0;
		Tile drop = null;

		// get one with the lowest F costs
		for (Tile t : todo.values()) {
			if (f == 0) {
				t.calculateBoth(startX, startY, startZ, endX, endY, endZ);
				f = t.getF();
				drop = t;
			} else {
				t.calculateBoth(startX, startY, startZ, endX, endY, endZ);
				double posF = t.getF();
				if (posF < f) {
					f = posF;
					drop = t;
				}
			}
		}

		this.todo.remove(drop.getID());
		if (!done.containsKey(drop.getID())) {
			done.put(drop.getID(), drop);
		}
		return drop;
	}

	// pass in the current tile as the parent
	private void processAdjacentTiles(Tile current) {

		// set of possible walk to locations adjacent to current tile
		Set<Tile> possible = new HashSet<Tile>();

		for (byte x = -1; x <= 1; x++) {
			for (byte y = -1; y <= 1; y++) {
				for (byte z = -1; z <= 1; z++) {

					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					if((x == 1 && z == 1) || (x == -1 && z == -1) || (x == 1 && z == -1) || (x == -1 && z == 1)){
						// don't go diagonally for more naturally looking movement
						continue;
					}

					Tile t = new Tile((short) (current.getX() + x), (short) (current.getY() + y), (short) (current.getZ() + z), current);

					if (!t.isInRange(this.range)) {
						continue;
					}

					if (done.containsKey(t.getID())) {
						// has already been processed
						continue;
					}

					// skip this tile if there can't be walked on this tile
					if (this.isWalkable(t)) {
						t.calculateBoth(startX, startY, startZ, endX, endY, endZ);
						possible.add(t);
					}

				}
			}
		}

		for (Tile t : possible) {
			// get the reference of the object in the array
			Tile openRef = null;
			if ((openRef = (todo.containsKey(t.getID()) ? todo.get(t.getID()) : null)) == null) {
				// not on open list, so add
				if (!todo.containsKey(t.getID())) {
					todo.put(t.getID(), t);
				}
			} else {
				// check if G costs are lower
				if (t.getG() < openRef.getG()) {
					// if current path seems to be better --> change parent
					openRef.setParent(current);
					// force updates of G and H values.
					openRef.calculateBoth(startX, startY, startZ, endX, endY, endZ);
				}

			}
		}

	}

	private boolean isWalkable(Tile t) {
		Location l = new Location(world, (startX + t.getX()), (startY + t.getY()), (startZ + t.getZ()));
		Block b = l.getBlock();
		int id = b.getBlock().getId();

		if (id != 10 && id != 11 && id != 51 && id != 59 && id != 65 && id != 0 && id != 85 && id != 107 && id != 113 && !this.canWalkThrough(id)) {
			// make sure the blocks above are not solid
			return (this.canWalkThrough(b.getRelative(0, 1, 0).getBlock().getId()) && b.getRelative(0, 2, 0).getBlock().getId() == 0);

		} else {
			return false;
		}
	}

	private boolean isWalkable(Location l) {
		Block b = l.getBlock();
		int id = b.getBlock().getId();

		if (id != 10 && id != 11 && id != 51 && id != 59 && id != 65 && id != 0 && !this.canWalkThrough(id)) {
			// make sure the blocks above are air or can be walked through
			return (this.canWalkThrough(b.getRelative(0, 1, 0).getBlock().getId()) && this.canWalkThrough(b.getRelative(0, 2, 0).getBlock().getId()));
		} else {
			return false;
		}
	}

	private boolean canWalkThrough(int id) {
		return (id == 0 || id == 6 || id == 50 || id == 63 || id == 30 || id == 31 || id == 32 || id == 37 || id == 38 || id == 39 || id == 40 || id == 55 || id == 66 || id == 75 || id == 76 || id == 78);
	}
}
