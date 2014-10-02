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

import java.util.Iterator;
import org.enderstone.server.api.ChatPosition;
import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Particle;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.inventory.InventoryHandler;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;

public interface Player {

	/**
	 * Sends a message to the selected chat position
	 * 
	 * @param message the message to send
	 * @param position the chat position to send the message to
	 */
	public void sendMessage(Message message, ChatPosition position);

	/**
	 * Get the player's name.
	 * 
	 * @return The player's name
	 */
	public String getPlayerName();

	/**
	 * Get the player's current food level.
	 * 
	 * @return The player's current food level
	 */
	public int getFoodLevel();

	/**
	 * Set the player's current food level.
	 * 
	 * @param foodLevel The food level to set the player to
	 */
	public void setFoodLevel(int foodLevel);

	/**
	 * Kick the player with a reason
	 * 
	 * @param reason the reason the player was kicked
	 */
	public void kick(Message reason);

	/**
	 * Find out if the player has the ability to fly.
	 * 
	 * @return true if the player can fly
	 */
	public boolean getAllowFlight();

	/**
	 * Set Whether or not the player should be allowed to fly.
	 * 
	 * @param allowFlight Whether or not the player should be allowed to fly
	 */
	public void setAllowFlight(boolean allowFlight);

	/**
	 * Checks to see if the player is flying.
	 * 
	 * @return true if the player is flying
	 */
	public boolean isFlying();

	/**
	 * Set whether or not the player should be flying.
	 * 
	 * @param flying Whether or not the player should be flying
	 */
	public void setFlying(boolean flying);

	/**
	 * Get the speed the player will fly at.
	 * 
	 * @return The speed the player will fly at
	 */
	public float getFlySpeed();

	/**
	 * Set the speed the player will fly at.
	 * 
	 * @param speed The speed the player will fly at
	 */
	public void setFlySpeed(float speed);

	/**
	 * Get the speed the player will speed at.
	 * 
	 * @return The speed the player will speed at
	 */
	public float getWalkSpeed();

	/**
	 * Set the speed the player will speed at.
	 * 
	 * @param speed The speed the player will speed at
	 */
	public void setWalkSpeed(float speed);

	/**
	 * Get the experience of the player.
	 * 
	 * @return The experience of the player
	 */
	public int getExperience();

	/**
	 * Get the experience level of the player
	 * 
	 * @return The experience level of the player
	 */
	public int getExperienceLevel();

	/**
	 * Set the experience of the player.
	 * 
	 * @param experience The experience to set the player to.
	 */
	public void setExperience(int experience);

	/**
	 * Set the experience level of the player.
	 * 
	 * @param experienceLevel The experience level to set the player to.
	 */
	public void setExperienceLevel(int experienceLevel);

	/**
	 * Checks to see if the player is able to sprint.
	 * 
	 * @return true if the player can sprint
	 */
	public boolean canSprint();

	/**
	 * Checks to see if the player is sprinting.
	 * 
	 * @return true if the player is sprinting
	 */
	public boolean isSprinting();

	/**
	 * Checks to see if the player is on the ground.
	 * 
	 * @return true if the player is on the ground
	 */
	public boolean isOnGround();

	/**
	 * Checks to see if the player is sneak.
	 * 
	 * @return true if the player is sneaking
	 */
	public boolean isSneaking();

	/**
	 * Set whether the player is sneaking server side.
	 * 
	 * @param sneaking Whether or not the player should be sneaking
	 */
	public void setSneaking(boolean sneaking);

	/**
	 * Play a sound to this player.
	 * 
	 * @param soundName the sound to play.
	 * @param volume the volume to play the sound at
	 * @param pitch the pitch to play the sound at
	 */
	public void playSound(String soundName, int volume, int pitch);

	/**
	 * Display a particle to the player.
	 * 
	 * @param particle the particle to display
	 * @param location the location to display the particle at
	 * @param xOffset the X offset of the particle
	 * @param yOffset the Y offset of the particle
	 * @param zOffset the Z offset of the particle
	 * @param data usually the velocity of the particle
	 * @param amount thr amount of particles to display
	 */
	public void playParticle(Particle particle, Location location, float xOffset, float yOffset, float zOffset, float data, int amount);

	/**
	 * Send a client side change to the plauyer.
	 * 
	 * @param loc the location of the block to change
	 * @param blockId the block Id to set the block to
	 * @param dataValue the data value to set the block to
	 */
	public void sendBlockUpdate(Location loc, BlockId blockId, byte dataValue);

	/**
	 * Get the player's display name.
	 *
	 * @return The player's display name
	 */
	public String getDisplayName();

	/**
	 * Set the player's display name.
	 *
	 * @param displayName the display name to set the player to
	 */
	public void setDisplayName(String displayName);

	/**
	 * Close the player's inventory.
	 */
	public void closeInventory();

	/***
	 * Get the player's game mode.
	 * 
	 * @return The player's gamemode
	 */
	public GameMode getGameMode();

	/**
	 * Set the player's gamemode.
	 *
	 * @param mode The gamemode to set the player to
	 */
	public void setGameMode(GameMode mode);

	/**
	 * Get the player's inventory.
	 * 
	 * @return The player's inventory
	 */
	public InventoryHandler getInventory();

	/**
	 * Get the item in the player's hand.
	 * 
	 * @return The item in the player's hand
	 */
	public ItemStack getItemInHand();

	/**
	 * Checks if the player has a permission.
	 * 
	 * @param permission permission to check for
	 * 
	 * @return true if the player has the permission
	 */
	boolean hasPermission(String permission);

	/**
	 * Award an achievment to this player.
	 * 
	 * @param name The name of the achievment to reward
	 */
	public void awardAchievment(String name);

	/**
	 * Updates a statistic for this player.
	 * 
	 * @param name The name of the statistic to update
	 * @param value The value to update the statistic by
	 */
	public void updateStatistic(String name, int value);

	/**
	 * Forces the player to send a chat message.
	 * 
	 * @param rawMessage the message to send
	 */
	public void forceChat(String rawMessage);

	/**
	 * Checks if the player is in the air.
	 * 
	 * @return true if the player is in the air
	 */
	public boolean isInAir();

	/**
	 * Sets the player's header on the tablist to a message.
	 * 
	 * @param header the message to set the header to
	 */
	public void setTabListHeader(Message header);

	/**
	 * Sets the player's footer on the tablist to a message.
	 * 
	 * @param footer the measage to set the footer to
	 */
	public void setTabListFooter(Message footer);

	/**
	 * Sets the players header and footer on the tablist to a message.
	 * 
	 * @param header the message to set the header to
	 * @param footer the message to set the footer to
	 */
	public void setTabListHeaderAndFooter(Message header, Message footer);

	/**
	 * Get the player's tablist's header.
	 * 
	 * @return The player's tablist's header
	 */
	public Message getTabListHeader();

	/**
	 * Get the player's tablist's footer.
	 * 
	 * @return The player's tablist's footer
	 */
	public Message getTabListFooter();

	/**
	 * Checks if the player is on fire.
	 * 
	 * @return true if the player is on fire.
	 */
	public boolean isOnFire();

	/**
	 * Checks if the player is eating.
	 * 
	 * @return true if the player is eating
	 */
	public boolean isEating();

	/**
	 * Set whether or not the player will sprint.
	 * 
	 * @param sprinting whether or not the player will sprint
	 */
	public void setSprinting(boolean sprinting);

	/**
	 * Display a welcome title to the player.
	 * 
	 * @param title The main message to display to the player
	 * @param subtitle The sub message to display to the player
	 */
	public void displayWelcomeTitle(Message title, Message subtitle);
	
	/**
	 * returns an iterator with the locations of the blocks a player is looking at
	 * 
	 * @param range
	 * @return iterator with the locations of the blocks a player is looking at
	 */
	
	public Iterator<Location> getLineOfSight(double range);
	
	/**
	 * returns the location of the block a player is looking at
	 * 
	 * @param range
	 * @return the location of the block a player is looking at (ignores solid blocks)
	 */
	public Location getTargetBlock(double range);
}
