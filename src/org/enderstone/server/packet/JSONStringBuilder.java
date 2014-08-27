package me.bigteddy98.mcserver.packet;

import org.json.JSONObject;

public class JSONStringBuilder {

	public static String build(String text) {
		JSONObject json = new JSONObject();
		json.put("text", text);
		return json.toString();
	}

	public static String read(String json) {
		JSONObject object = new JSONObject(json);
		return object.getString("name");
	}
}
