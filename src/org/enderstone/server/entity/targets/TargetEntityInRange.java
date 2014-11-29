/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.entity.targets;

import java.util.Collection;
import org.enderstone.server.api.entity.Entity;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.entity.EntityMob;
import org.enderstone.server.entity.player.EnderPlayer;

/**
 *
 * @author gyroninja
 */
public class TargetEntityInRange implements Target {

	private final EntityMob mob;

	private final Class<? extends Entity> targetType;

	public TargetEntityInRange(EntityMob mob, Class<? extends Entity> targetType) {

		this.mob = mob;

		this.targetType = targetType;
	}

	@Override
	public boolean start() {

		Collection<EnderEntity> entities = (Collection<EnderEntity>) (targetType == EnderPlayer.class ? mob.getWorld().getPlayers() : mob.getWorld().getEntities());

		for (Entity e : entities) {

			if (e.getClass().equals(targetType)) {

				if (mob.getLocation().distanceSquared(e.getLocation()) < 1024) {//32 squared = 1024

					mob.getNavigator().setTarget((EnderEntity) e);

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinue() {

		if (mob.getNavigator().getTarget().isDead() || (mob.getNavigator().getTarget() instanceof EnderPlayer && !((EnderPlayer) mob.getNavigator().getTarget()).isOnline)) {

			return false;
		}

		if (mob.getNavigator().getPath() == null) {

			return false;
		}

		return mob.getLocation().distanceSquared(mob.getNavigator().getTarget().getLocation()) < 1024;//32 squared = 1024
	}

	@Override
	public void reset() {

		mob.getNavigator().setTarget(null);
	}

	@Override
	public void run() {

		
	}
}
