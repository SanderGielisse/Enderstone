package org.enderstone.server.entity;

public enum GameMode {
	SURVIVAL(0), CREATIVE(1), ADVENTURE(2);

	private int id;

	private GameMode(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
