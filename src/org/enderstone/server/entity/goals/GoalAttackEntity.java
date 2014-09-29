/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.entity.goals;

import java.util.Collection;
import java.util.List;
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

		pathfindToTarget();
	}

	@Override
	public void run() {

		lastUpdate++;

		if (lastUpdate > 20) {

			lastUpdate = 0;

			pathfindToTarget();
		}
	};

	@Override
	public void reset() {

		target = null;

		mob.getNavigator().setPath(null, null);
	}

	private void pathfindToTarget() {

		PathFinder pathfinder = new PathFinder(mob.getLocation(), target.getLocation(), 32);

		List<PathTile> path = pathfinder.getPath();

		if (pathfinder.hasPath()) {

			mob.getNavigator().setPath(pathfinder.getStartLocation(), path);
		}

		else {

			mob.getNavigator().setPath(null, null);
		}
	}
}
