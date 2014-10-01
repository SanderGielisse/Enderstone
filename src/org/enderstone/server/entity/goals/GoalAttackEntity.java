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

import java.util.Collection;
import java.util.List;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.entity.Entity;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.EntityMob;
import org.enderstone.server.entity.pathfinding.PathFinder;
import org.enderstone.server.entity.pathfinding.PathTile;

/**
 *
 * @author gyroninja
 */
public class GoalAttackEntity implements Goal {

	private final EntityMob mob;
	private final Class<? extends Entity> targetType;

	private EnderEntity target;

	private int lastUpdate;

	public GoalAttackEntity(EntityMob mob, Class<? extends Entity> targetType) {

		this.mob = mob;
		this.targetType = targetType;
	}

	@Override
	public boolean shouldStart() {

		Collection<EnderEntity> entities = (Collection<EnderEntity>) (targetType == EnderPlayer.class ? mob.getWorld().getPlayers() : mob.getWorld().getEntities());

		for (Entity e : entities) {

			if (e.getClass().equals(targetType)) {

				if (mob.getLocation().distanceSquared(e.getLocation()) < 1024) {//32 squared = 1024

					target = (EnderEntity) e;

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinue() {

		return mob.getLocation().distanceSquared(target.getLocation()) < 1024;//32 squared = 1024
	}

	@Override
	public void start() {

		pathfindToTarget(mob.getLocation());
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

		target = null;

		mob.getNavigator().setPath(null, null);
	}

	private void pathfindToTarget(Location start) {

		PathFinder pathfinder = new PathFinder(mob.getLocation(), target.getLocation(), 32);

		List<PathTile> path = pathfinder.getPath();

		if (pathfinder.hasPath()) {

			mob.getNavigator().setPath(pathfinder, path);
		}

		else {

			mob.getNavigator().setPath(null, null);
		}
	}
}
