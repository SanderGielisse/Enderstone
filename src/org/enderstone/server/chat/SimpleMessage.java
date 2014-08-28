/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.enderstone.server.chat;

import org.enderstone.server.entity.ChatColor;
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
