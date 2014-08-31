package org.enderstone.server.uuid;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.entity.PlayerTextureStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UUIDFactory {

	private final Map<String, UUID> uuidCache = new HashMap<>();
	private final Map<UUID, PlayerTextureStore> textureCache = new HashMap<>();

	public UUID getPlayerUUIDAsync(String name) {
		synchronized (uuidCache) {
			if (uuidCache.containsKey(name)) {
				return uuidCache.get(name);
			}
		}
		try {
			String URL = "https://api.mojang.com/users/profiles/minecraft/" + name;
			JSONObject json = new ServerRequest(URL).get();
			UUID uuid;
			if (json == null)
				return null;
			else
				uuid = parseUUID(json.getString("id"));
			synchronized (uuidCache) {
				this.uuidCache.put(name, uuid);
			}
			return uuid;
		} catch (JSONException | IOException e) {
			// something went wrong
			return null;
		}
	}

	public PlayerTextureStore getTextureDataAsync(UUID uuid) {
		synchronized (textureCache) {
			if (textureCache.containsKey(uuid)) {
				return textureCache.get(uuid);
			}
		}

		try {
			String URL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false";
			PlayerTextureStore store = new PlayerTextureStore(new ServerRequest(URL).get().getJSONArray("properties"));

			synchronized (textureCache) {
				textureCache.put(uuid, store);
			}
			return store;
		} catch (JSONException | IOException e) {
			EnderLogger.exception(e);
			return null;
		}
	}

	public static UUID parseUUID(String uuidStr) {
		String[] uuidComponents = new String[]{uuidStr.substring(0, 8), uuidStr.substring(8, 12), uuidStr.substring(12, 16), uuidStr.substring(16, 20), uuidStr.substring(20, uuidStr.length())};
		StringBuilder builder = new StringBuilder();
		for (String component : uuidComponents) {
			builder.append(component).append('-');
		}
		builder.setLength(builder.length() - 1);
		return UUID.fromString(builder.toString());
	}
}
