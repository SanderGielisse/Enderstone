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
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.regions.EnderWorld;

public interface Entity {

	public void teleport(Location loc);

	public void teleport(EnderEntity e);

	public Location getLocation();

	public float getHealth();

	public void setHealth(float health);

	public float getMaxHealth();

	public void setMaxHealth(float maxhealth);

	public EnderWorld getWorld();

}
