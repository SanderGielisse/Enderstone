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
package org.enderstone.server.uuid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.entity.PlayerTextureStore;
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
			EnderLogger.warn("Unable to find a uuid for OfflinePlayerUUID: "+name);
			EnderLogger.warn(e.getMessage());
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
			EnderLogger.warn("Unable to find a skin for OfflinePlayerUUID: "+uuid);
			EnderLogger.warn(e.getMessage());
			return PlayerTextureStore.DEFAULT_STORE;
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
