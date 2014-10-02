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
package org.enderstone.server.api.event.entity;

import org.enderstone.server.api.event.Cancellable;
import org.enderstone.server.api.event.Event;
import org.enderstone.server.entity.EnderEntity;

/**
 *
 * @author gyroninja
 */
public class EntityDamageEvent extends Event implements Cancellable {

	private boolean cancelled = false;
	private final EnderEntity entity;
	private float damage;

	/**
	 * EntityDamageEvent is called when an entity takes damage.
	 * 
	 * @param entity the entity that took damage
	 * @param damage the damage the entity took
	 */
	public EntityDamageEvent(EnderEntity entity, float damage) {
		this.entity = entity;
		this.damage = damage;
	}

	/**
	 * Gets the entity that took damage.
	 * 
	 * @return The entity that took damage
	 */
	public EnderEntity getEntity() {
		return entity;
	}

	/**
	 * Gets the damage the entity took.
	 * 
	 * @return The damage the entity took
	 */
	public float getDamage() {
		return damage;
	}

	/**
	 * Sets the damage the entity will take.
	 * 
	 * @param damage the damage the entity will take
	 */
	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
