package me.bigteddy98.mcserver.packet;

import org.json.JSONObject;

public class JSONStringBuilder {

	public static String build(String text) {
		return "{\"text\": \"" + text + "\"}";
	}

	public static String read(String json) {
		JSONObject object = new JSONObject(json);
		return object.getString("name");
	}
}
