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
package org.enderstone.server.entity.targets;

import java.util.Collection;
import org.enderstone.server.api.GameMode;

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
		@SuppressWarnings("unchecked")
		Collection<EnderEntity> entities = (Collection<EnderEntity>) (targetType == EnderPlayer.class ? mob.getWorld().getPlayers() : mob.getWorld().getEntities());
		for (Entity e : entities) {
			if (e.getClass().equals(targetType)) {
				if (e instanceof EnderPlayer) {
					if (((EnderPlayer) e).getGameMode() == GameMode.CREATIVE || ((EnderPlayer) e).getGameMode() == GameMode.SPECTATOR) {
						continue;
					}
				}
				if (mob.getLocation().distanceSquared(e.getLocation()) < 1024) { // 32 squared = 1024
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
		if (mob.getNavigator().getTarget() instanceof EnderPlayer && (((EnderPlayer) mob.getNavigator().getTarget()).getGameMode() == GameMode.CREATIVE || ((EnderPlayer) mob.getNavigator().getTarget()).getGameMode() == GameMode.SPECTATOR)) {
			return false;
		}
		if (mob.getNavigator().getPath() == null) {
			return false;
		}
		return mob.getLocation().distanceSquared(mob.getNavigator().getTarget().getLocation()) < 1024; // 32 squared = 1024
	}

	@Override
	public void reset() {
		mob.getNavigator().setTarget(null);
	}

	@Override
	public void run() {}
}
