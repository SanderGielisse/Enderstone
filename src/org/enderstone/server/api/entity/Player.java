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

import org.enderstone.server.api.ChatPosition;
import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Particle;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.inventory.InventoryHandler;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderWorld;

public interface Player {

	public void sendMessage(Message message, ChatPosition position);

	public String getPlayerName();

	public int getFoodLevel();

	public void setFoodLevel(int foodLevel);

	public void kick(String reason);

	public boolean getAllowFlight();

	public void setAllowFlight(boolean allowFlight);

	public boolean isFlying();

	public void setFlying(boolean flying);

	public float getFlySpeed();

	public void setFlySpeed(float speed);

	public float getWalkSpeed();

	public void setWalkSpeed(float speed);

	public int getExperience();

	public int getExperienceLevel();

	public void setExperience(int experience);

	public void setExperienceLevel(int experienceLevel);

	public boolean canSprint();

	public boolean isOnGround();

	public boolean isSneaking();

	public void setSneaking(boolean sneaking);

	public void playSound(String soundName, int volume, int pitch);

	public void playParticle(Particle particle, Location location, float xOffset, float yOffset, float zOffset, float data, int amount);

	public void sendBlockUpdate(Location loc, BlockId blockId, byte dataValue);

	public String getDisplayName();

	public void setDisplayName(String displayName);

	public void closeInventory();

	public GameMode getGameMode();

	public void setGameMode(GameMode mode);

	public InventoryHandler getInventory();

	public ItemStack getItemInHand();

	boolean hasPermission(String permission);

	public void awardAchievment(String name);

	public void updateStatistic(String name, int value);

	public void forceChat(String rawMessage);

	public boolean isInAir();

	public void setTabListHeader(Message header);

	public void setTabListFooter(Message footer);

	public void setTabListHeaderAndFooter(Message header, Message footer);

	public Message getTabListHeader();

	public Message getTabListFooter();
}