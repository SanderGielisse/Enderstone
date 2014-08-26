package me.bigteddy98.mcserver.packet;

public class JSONStringBuilder {

	public static String build(String text) {
		return "{\"text\": \"" + text + "\"}";
	}
}
