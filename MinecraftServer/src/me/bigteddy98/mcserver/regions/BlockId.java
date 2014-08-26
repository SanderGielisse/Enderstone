/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.bigteddy98.mcserver.regions;

/**
 *
 * @author Fernando
 */
public class BlockId {

	public static final BlockId air = new BlockId("air", (short) 0);
	public static final BlockId stone = new BlockId("stone", (short) 1);
	public static final BlockId grass = new BlockId("grass", (short) 2);
	public static final BlockId dirt = new BlockId("dirt", (short) 3);
	public static final BlockId cobblestone = new BlockId("cobblestone", (short) 4);
	public static final BlockId woodenplank = new BlockId("woodenplank", (short) 5);

	private String name;
	private short id;
	private Class<?> dataClass;

	private BlockId(String name, short id) {
		this(name, id, null);
	}

	private BlockId(String name, short id, Class<?> clazz) {
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
		return id;
	}
}
