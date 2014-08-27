/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.enderstone.server.regions;

/**
 *
 * @author Fernando
 */
public enum BlockId {

	AIR("air", 0), STONE("stone", 1), GRASS("grass", 2), DIRT("dirt", 3), COBBLESTONE("cobblestone", 4), WOODEN_PLANK("woodenplank", 5), WATER("water", 9), SAND("sand", 12), SANDSTONE("sandstone", 24);

	private String name;
	private int id;
	private Class<?> dataClass;

	private BlockId(String name, int id) {
		this(name, id, null);
	}

	private BlockId(String name, int id, Class<?> clazz) {
		this.name = name;
		this.id = id;
		this.dataClass = clazz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getDataClass() {
		return dataClass;
	}

	public short getId() {
		return (short) id;
	}

	public static BlockId byId(short s) {
		for (BlockId id : values()) {
			if (id.getId() == s) {
				return id;
			}
		}
		throw new RuntimeException("Unsupported block id " + s);
	}
}
