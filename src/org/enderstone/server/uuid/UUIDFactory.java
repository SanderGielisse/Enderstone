package org.enderstone.server.uuid;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.enderstone.server.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UUIDFactory {

	private final Map<String, UUID> uuidCache = Collections.synchronizedMap(new HashMap<String, UUID>());
	private final Map<UUID, JSONObject> textureCache = Collections.synchronizedMap(new HashMap<UUID, JSONObject>());

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
			if(json == null)
				uuid = Main.getInstance().onlineMode ? null : UUID.randomUUID();
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

	public JSONObject getTextureDataAsync(UUID uuid) {
		synchronized (textureCache) {
			if (textureCache.containsKey(uuid)) {
				return textureCache.get(uuid);
			}
		}

		try {
			String URL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false";
			JSONArray properties = new ServerRequest(URL).get().getJSONArray("properties");
			for (int i = 0; i < properties.length(); i++) {
				JSONObject property = properties.getJSONObject(i);
				if (property.getString("name").equals("textures")) {
					synchronized (textureCache) {
						textureCache.put(uuid, property);
					}
					return property;
				}
			}
			return null;
		} catch (JSONException | IOException e) {
			// something went wrong
			return null;
		}
	}

	private static UUID parseUUID(String uuidStr) {
		String[] uuidComponents = new String[] { uuidStr.substring(0, 8), uuidStr.substring(8, 12), uuidStr.substring(12, 16), uuidStr.substring(16, 20), uuidStr.substring(20, uuidStr.length()) };
		StringBuilder builder = new StringBuilder();
		for (String component : uuidComponents) {
			builder.append(component).append('-');
		}
		builder.setLength(builder.length() - 1);
		return UUID.fromString(builder.toString());
	}
}
