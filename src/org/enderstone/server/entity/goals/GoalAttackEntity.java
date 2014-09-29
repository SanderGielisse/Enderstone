/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.entity.goals;

import java.util.List;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.entity.EntityMob;
import org.enderstone.server.entity.pathfinding.PathFinder;
import org.enderstone.server.entity.pathfinding.PathTile;

/**
 *
 * @author gyroninja
 */
public class GoalAttackEntity implements Goal {

	private final EntityMob mob;
	private final EnderEntity target;

	public GoalAttackEntity(EntityMob mob, EnderEntity target) {

		this.mob = mob;
		this.target = target;
	}

	@Override
	public boolean shouldStart() {

		return mob.getLocation().distanceSquared(target.getLocation()) < 1024;//32 squared = 1024
	}

	@Override
	public boolean shouldContinue() {

		return mob.getLocation().distanceSquared(target.getLocation()) < 1024;//32 squared = 1024
	}

	@Override
	public void start() {

		PathFinder pathfinder = new PathFinder(mob.getLocation(), target.getLocation(), 32);

		List<PathTile> path = pathfinder.getPath();

		if (pathfinder.hasPath()) {

			mob.getNavigator().setPath(path);
		}

		else {

			reset();
		}
	}

	@Override
	public void run() {};

	@Override
	public void reset() {

		mob.getNavigator().setPath(null);
	}
}
