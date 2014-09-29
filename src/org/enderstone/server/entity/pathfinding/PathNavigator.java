/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.entity.pathfinding;

import java.util.ArrayList;
import java.util.List;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.entity.EntityMob;
import org.enderstone.server.entity.goals.Goal;
import org.enderstone.server.entity.targets.Target;

/**
 *
 * @author gyroninja
 */
public class PathNavigator {

	private final EntityMob mob;

	private final List<Goal> goals = new ArrayList<>();
	private final List<Target> targets = new ArrayList<>();

	private Goal currentGoal;
	private Target currentTarget;

	private Location start;

	private List<PathTile> path;
	private int currentTile = 0;

	public PathNavigator(EntityMob mob) {

		this.mob = mob;
	}

	public List<PathTile> getPath() {

		return path;
	}

	public void setPath(Location start, List<PathTile> path) {

		this.start = start;

		this.path = path;
	}

	//Goals should be added in order of priority
	public void addGoal(Goal goal) {

		goals.add(goal);
	}

	//Targets should be added in order of priority
	public void addTarget(Target target) {

		targets.add(target);
	}

	public void run() {

		updateCurrentGoal();
		updateCurrentTarget();

		if (currentGoal != null) {

			currentGoal.run();
		}

		if (currentTarget != null) {

			currentTarget.run();
		}

		if (path != null) {

			if (mob.getLocation().isInRange(1, path.get(currentTile).getLocation(start), false)) {

				if (path.size() == currentTile + 1) {

					return;
				}

				currentTile++;
			}

			Location nextDestination = path.get(currentTile).getLocation(start);

			Vector difference = Vector.substract(nextDestination, mob.getLocation());

			int speed = (int) (mob.getMovementSpeed() / 20);//20 ticks per second

			difference.setX(difference.getX() > speed ? difference.getX() : (difference.getX() > 0 ? speed : -speed));
			difference.setY(difference.getY() > speed ? difference.getY() : (difference.getY() > 0 ? speed : -speed));
			difference.setZ(difference.getZ() > speed ? difference.getZ() : (difference.getZ() > 0 ? speed : -speed));

			//move mob by vector
		}
	}

	private void updateCurrentGoal() {

		if (currentGoal == null) {

			for (Goal goal : goals) {

				if (goal.shouldStart()) {

					currentGoal = goal;

					break;
				}
			}
		}

		else {

			if (!currentGoal.shouldContinue()) {

				currentGoal = null;
			}
		}
	}

	private void updateCurrentTarget() {

		if (currentTarget == null) {

			for (Target target : targets) {

				if (target.shouldStart()) {

					currentTarget = target;

					break;
				}
			}
		}

		else {

			if (!currentTarget.shouldContinue()) {

				currentTarget = null;
			}
		}
	}
}
