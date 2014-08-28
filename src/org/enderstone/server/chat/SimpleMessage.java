package org.enderstone.server.chat;

import org.json.JSONObject;

/**
 *
 * @author Fernando
 */
public class SimpleMessage implements Message {

	private final String text;
	private final ThreadLocal<JSONObject> buildCache = new ThreadLocal<JSONObject>() {

		@Override
		protected JSONObject initialValue() {
			return new JSONObject();
		}
	};

	public SimpleMessage(String text) {
		this.text = text;
	}

	@Override
	public String toPlainText() {
		return text;
	}

	@Override
	public String toMessageJson() {
		JSONObject json = buildCache.get();
		json.put("text", text);
		return json.toString();
	}

	@Override
	public String toAsciiText() {
		return toPlainText();
	}
	
	public AdvancedMessagePart color(ChatColor ... colors)
	{
		return new AdvancedMessage(text).combineColor(colors);
	}

}
