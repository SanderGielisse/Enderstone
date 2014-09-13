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

import org.enderstone.server.api.GameMode;
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.api.messages.ChatColor;
import org.enderstone.server.api.messages.Message;

public class PlayerSettings {
	public String displayName = null;
	public String locale = "en_US";
	public byte renderDistance = 3;
	public byte chatFlags = 0;
	public boolean chatColors = true;
	public int displayedSkinParts = 0;
	public GameMode gameMode = GameMode.SURVIVAL;
	public boolean isCreative = false;
	public boolean godMode = false;
	public boolean allowFlight = true;
	public boolean isFlying = true;
	public boolean isOnFire = false;
	public boolean isSneaking = false;
	public boolean isSprinting = false;
	public boolean isEating = false;
	public boolean isInvisible = false;
	public float flySpeed = 0.05F;
	public float walkSpeed = 0.1F;
	public int food = 20;
	public float foodSaturation = 0;
	public int experience = 0;
	public Message tabListHeader = new AdvancedMessage("Player Tab-List").setColor(ChatColor.GOLD).setBold(true).build();
	public Message tabListFooter = new AdvancedMessage("This server is proudly powered by Enderstone").setColor(ChatColor.RED).setItalic(true).build();
}