package me.bigteddy98.mcserver.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataWatcher {

	private final Map<Integer, Object> watched = Collections.synchronizedMap(new HashMap<Integer, Object>());

	public synchronized void watch(int index, Object value) {
		this.watched.put(index, value);
	}

	public Map<Integer, Object> getWatchedCopy() {
		synchronized (watched) {
			return new HashMap<>(watched);
		}
	}
}
