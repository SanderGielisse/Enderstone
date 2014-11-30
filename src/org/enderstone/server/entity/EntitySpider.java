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
package org.enderstone.server.entity;

import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.entity.goals.Goal;
import org.enderstone.server.entity.goals.GoalAttackEntity;
import org.enderstone.server.entity.targets.TargetEntityInRange;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.regions.EnderWorld;

public class EntitySpider extends EntityMob {

	private static final byte APPEARANCE_ID = 52;

	public EntitySpider(EnderWorld world, Location location) {
		super(APPEARANCE_ID, world, location);
		this.getNavigator().addGoal(new GoalAttackEntity(this));
		this.getNavigator().addTarget(new TargetEntityInRange(this, EnderPlayer.class));
	}

	@Override
	protected String getDamageSound() {
		return "mob.spider.say";
	}

	@Override
	protected String getDeadSound() {
		return "mob.spider.death";
	}

	@Override
	protected String getRandomSound() {
		if (Main.random.nextBoolean()) {
			return "mob.spider.say";
		} else {
			return "mob.spider.step";
		}
	}

	@Override
	public float getMovementSpeed() {
		return 3;
	}

	private long previousJumpTick = 0;

	@Override
	public boolean onCollide(EnderPlayer withPlayer) {
		for (Goal pathfinder : this.getNavigator().getGoals()) {
			if (pathfinder instanceof GoalAttackEntity) {
				if (super.getNavigator().getTarget() != null && super.getNavigator().getTarget() instanceof EnderPlayer) {
					if (super.getNavigator().getTarget().equals(withPlayer)) {
						if ((Main.getInstance().getCurrentServerTick() - this.previousJumpTick) > (20)) { // 1 second
							this.previousJumpTick = Main.getInstance().getCurrentServerTick();
							Location newLoc = this.getLocation();
							newLoc.setYaw((float) Location.calcYaw(this.getLocation(), withPlayer.getLocation()));
							this.setLocation(newLoc);
							this.broadcastRotation(newLoc.getPitch(), newLoc.getYaw());
							this.setVelocity(Vector.substractAndNormalize(this.getLocation(), withPlayer.getLocation()).multiply(2F).setY(0.3F));
							withPlayer.damage(1F, Vector.substractAndNormalize(this.getLocation(), withPlayer.getLocation()).multiply(0.4F).setY(0.3F));
						}
					}
				}
			}
		}
		return false;
	}
}
