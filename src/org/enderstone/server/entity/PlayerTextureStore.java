package org.enderstone.server.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ferrybig
 */
public class PlayerTextureStore implements java.io.Serializable{
	
	private static final long serialVersionUID = 45734736L;
	private static final String DEFAULT_SKIN_SIGNATURE = "eyJ0aW1lc3RhbXAiOjE0MDkwODUzMTUyOTUsInByb2ZpbGVJZCI6IjY3NDNhODE0OWQ0MTRkMzNhZjllZTE0M2JjMmQ0NjJjIiwicHJvZmlsZU5hbWUiOiJzYW5kZXIyNzk4IiwiaXNQdWJsaWMiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYTgwYTQyMzVkMzc1N2Q0YWI0Nzg2ZGY0NzQxYzE1MmExMWM5ZGVjMGU1YWM5ZmJlOGVmMmM0MjA4YWM2In19fQ==";
	private static final String DEFAULT_SKIN_VALUE = "T/S5/8yNblHMtt5KCnFwymwHOF9RCPh223CwCc3wAUoBRDmYJR2jtlkoLltKp24YZa/s/NTtuaji9g4Dq6hkDC+WvAHJ3UxWHSixumG78EJQxUIHW0QD7wmkeAb2RfipuXG84gnzJ6gFz3aYz7vNM7eZ1dO0KCDKVawsvMkHUvM2BoRUh/rSj0ji6BlQ611FU1peMXep9oAPOcKZFK0snH4Su0qZt8n3dw5087RuhaBGmkT4nYrD7eH43uGDdXs5SLWzLd1d3oQzj0cGL7GiM1Jrg8DcaQoXXqMMuMThviHVi1YVM/sZ7eWVj5Ui4BVOTu2nGSH5Avegq4UOdBILfHadlFroKPEX5uRA3Od+/3hF7ZGBYv+W9/oA8P6gUsnEvAYC4TnM5KWViCg/aJ/7hDYeW6Nv0CjHHz7o3iNy2OxeL3X4jhLSlYRg4gEkejohN5NUeFi1ZRxvhPgJLr2aVKYsMNtKcLfRI567NxuRpLt4KAd62zxB5AzfWJd3qIK8q8a9fIfqiDJ8UHdW801Dhg2HSqmf9xzw3RPqOTkAX3gCpxBsfHedPzScW7RBEoyqIk9LEx5dZuVUBHOlPS2kk/8zTvKWGhFfJKmyrL159ZElPR9DjZoNN1LBmIJEAZ3jRfwZBDZVux8xUYpsrh1vT3DTP+lUMoD0oql3M3i/Lgg=";
	private final Map<String, PlayerSkinData> data;
	
	public PlayerTextureStore() {
		this((JSONArray)null);
	}
	
	public PlayerTextureStore(JSONArray array) {
		if (array == null)
			this.data = DEFAULT_MAP;
		else {
			this.data = new HashMap<>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject keySet = array.getJSONObject(i);
				data.put(keySet.getString("name"), new PlayerSkinData(
						keySet.optString("signature", DEFAULT_SKIN_SIGNATURE),
						keySet.optString("value", DEFAULT_SKIN_VALUE)));
			}
			if(!this.data.containsKey("textures"))
			{
				this.data.put("textures", DEFAULT_SKIN);
			}
		}
	}
	
	public PlayerTextureStore(PlayerTextureStore other) {
		data = new HashMap<>(other.data);
	}
	public PlayerSkinData getSkin() {
		return data.get("textures");
	}
	
	public static final PlayerSkinData DEFAULT_SKIN = new PlayerSkinData(DEFAULT_SKIN_SIGNATURE, DEFAULT_SKIN_VALUE);
	private static final Map<String, PlayerSkinData> DEFAULT_MAP;
	
	static {
		DEFAULT_MAP = new HashMap<>();
		DEFAULT_MAP.put("textures", DEFAULT_SKIN);
	}
	
	public static final PlayerTextureStore DEFAULT_STORE = new PlayerTextureStore();

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.data);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final PlayerTextureStore other = (PlayerTextureStore) obj;
		return Objects.equals(this.data, other.data);
	}

	@Override
	public String toString() {
		return "PlayerTextureStore{" + "data=" + data + '}';
	}
	
}
