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
package org.enderstone.server.api;

public enum Particle {
	 
	EXPLOSION_NORMAL("explode", 0), 
    EXPLOSION_LARGE("largeexplode", 1), 
    EXPLOSION_HUGE("hugeexplosion", 2), 
    FIREWORKS_SPARK("fireworksSpark", 3), 
    WATER_BUBBLE("bubble", 4), 
    WATER_SPLASH("splash", 5), 
    WATER_WAKE("wake", 6), 
    SUSPENDED("suspended", 7), 
    SUSPENDED_DEPTH("depthsuspend", 8), 
    CRIT("crit", 9), 
    CRIT_MAGIC("magicCrit", 10), 
    SMOKE_NORMAL("smoke", 11), 
    SMOKE_LARGE("largesmoke", 12), 
    SPELL("spell", 13), 
    SPELL_INSTANT("instantSpell", 14), 
    SPELL_MOB("mobSpell", 15), 
    SPELL_MOB_AMBIENT("mobSpellAmbient", 16), 
    SPELL_WITCH("witchMagic", 17), 
    DRIP_WATER("dripWater", 18), 
    DRIP_LAVA("dripLava", 19), 
    VILLAGER_ANGRY("angryVillager", 20), 
    VILLAGER_HAPPY("happyVillager", 21), 
    TOWN_AURA("townaura", 22), 
    NOTE("note", 23),  
    PORTAL("portal", 24), 
    ENCHANTMENT_TABLE("enchantmenttable", 25),  
    FLAME("flame", 26),  
    LAVA("lava", 27),  
    FOOTSTEP("footstep", 28), 
    CLOUD("cloud", 29), 
    REDSTONE("reddust", 30),  
    SNOWBALL("snowballpoof", 31),  
    SNOW_SHOVEL("snowshovel", 32),  
    SLIME("slime", 33), 
    HEART("heart", 34), 
    BARRIER("barrier", 35), 
    WATER_DROP("droplet", 39), 
    ITEM_TAKE("take", 40), 
    MOB_APPEARANCE("mobappearance", 41);
    
    private final String name;
    private final int id;
   
	/**
	 * Creates a new element of Particle
	 * 
	 * @param name the name of the particle
	 * @param id the id of the new element
	 */
	private Particle(String name, int id) {
		this.name = name;
		this.id = id;
	}

	/**
	 * Get the name of the element.
	 * 
	 * @return The name of the element
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the id of the element.
	 * 
	 * @return The id of the element
	 */
	public int getId() {
		return id;
	}
}
