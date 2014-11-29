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
package org.enderstone.server.api.entity;

import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.api.World;
import org.enderstone.server.entity.EnderEntity;

public interface Entity {

	/**
	 * Teleports the entity to the given location.
	 * 
	 * @param loc The location to be teleported to
	 */
	public void teleport(Location loc);

	/**
	 * Teleports the entity to another entity.
	 * 
	 * @param loc The other entity to be teleported to
	 */
	public void teleport(EnderEntity e);

	/**
	 * Get the location of the entity.
	 * 
	 * @return The location of the entity
	 */
	public Location getLocation();

	/**
	 * Get the health of the entity.
	 * 
	 * @return The health of the entity
	 */
	public float getHealth();

	/**
	 * Set the health of the entity.
	 * 
	 * @param health The health to set the entity to
	 * 
	 * @return true if successfully set
	 */
	public boolean setHealth(float health);

	/**
	 * Get the maximum health of the entity.
	 * 
	 * @return The maximum health of the entity
	 */
	public float getMaxHealth();

	/**
	 * Set the maximum health of the entity,
	 * 
	 * @param maxhealth The maximum health to set the entity to
	 */
	public void setMaxHealth(float maxhealth);

	/**
	 * Get the world the entity is in.
	 * 
	 * @return The world the entity is in
	 */
	public World getWorld();

	/**
	 * Get the ticks that the entity will be on fire for.
	 * 
	 * @return The ticks that the entity will be on fire for.
	 */
	public int getFireTicks();

	/**
	 * Set the ticks the entity will be on fire for.
	 * 
	 * @param fireTicks The ticks the entity will be set on fire for
	 */
	public void setFireTicks(int fireTicks);
	
	/**
	 * Damage the entity
	 * 
	 * @param damage amount to damage the entity by
	 * 
	 * @return true if player survived damage
	 */
	public boolean damage(float damage);

	/**
	 * Damage the entity and knock back player.
	 * 
	 * @param damage amount to damage the entity by
	 * @param knockback vector to knockback the player by
	 * 
	 * @return true if player survived damage
	 */
	public boolean damage(float damage, Vector knockback);

	/**
	 * Removes the entity from the world.
	 * 
	 */
	public void remove();
	
	/**
	 * returns the location of the head of the entity
	 * 
	 * @return the location of the head of the entity
	 */
	public Location getHeadLocation();
	
	public boolean isOnGround();
}
