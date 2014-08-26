package me.bigteddy98.mcserver.entity;

import java.util.HashMap;
import java.util.Map;

public class DataWatcher {

	public Map<Integer, Object> watched = new HashMap<>();

	public void watch(int index, Object value) {
		this.watched.put(index, value);
	}

	public Map<Integer, Object> getWatched() {
		return watched;
	}
}
