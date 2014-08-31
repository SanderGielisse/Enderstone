package org.enderstone.server.entity;

import java.util.HashMap;
import java.util.Map;

public class DataWatcher {

	private final Map<Integer, Object> watched = new HashMap<Integer, Object>();

	public void watch(int index, Object value) {
		synchronized (watched) {
			this.watched.put(index, value);
		}
	}

	public Map<Integer, Object> getWatchedCopy() {
		synchronized (watched) {
			return new HashMap<>(watched);
		}
	}
}
