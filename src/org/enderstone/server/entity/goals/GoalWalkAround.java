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
package org.enderstone.server.entity.goals;

import java.util.List;

import org.enderstone.server.api.Location;
import org.enderstone.server.entity.EntityMob;
import org.enderstone.server.entity.pathfinding.PathFinder;
import org.enderstone.server.entity.pathfinding.PathTile;

public class GoalWalkAround implements Goal {

	private final EntityMob mob;
	private Location toLocation;

	private int lastUpdate;

	public GoalWalkAround(EntityMob mob, Location toLocation) {
		this.mob = mob;
		this.setToLocation(toLocation);
	}

	public void setToLocation(Location toLocation) {
		this.toLocation = toLocation;
	}

	@Override
	public boolean start() {
		if (mob.getNavigator().getTarget() == null) {
			return false;
		}
		pathfindToTarget(mob.getLocation());
		return mob.getNavigator().getPathfinder() != null && mob.getNavigator().getPathfinder().hasPath();
	}

	@Override
	public boolean shouldContinue() {
		return mob.getNavigator().getPath() != null;
	}

	@Override
	public void run() {
		lastUpdate++;
		if (lastUpdate > 20) {
			lastUpdate = 0;
			PathTile currentTile = mob.getNavigator().getCurrentTile();
			if (currentTile != null) {
				pathfindToTarget(currentTile.getLocation(mob.getNavigator().getPathfinder().getStartLocation()));
			}
		}
	};

	@Override
	public void reset() {
		mob.getNavigator().setPath(null, null);
	}

	private void pathfindToTarget(Location start) {
		if (mob.getNavigator().getTarget() != null && this.toLocation != null && !isTooClose(this.toLocation, (this.mob.getLocation()))) {
			PathFinder pathfinder = new PathFinder(mob, mob.getLocation(), this.toLocation, 32);
			List<PathTile> path = pathfinder.calculatePath();
			if (pathfinder.hasPath()) {
				mob.getNavigator().setPath(pathfinder, path);
			} else {
				mob.getNavigator().setPath(null, null);
			}
		} else {
			mob.getNavigator().setPath(null, null);
		}
	}

	private boolean isTooClose(Location loc1, Location loc2) {
		return loc1.distanceSquared(loc2) < 4; // too close
	}
}