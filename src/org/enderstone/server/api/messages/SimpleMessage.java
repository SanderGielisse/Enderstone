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
package org.enderstone.server.api.messages;

import org.enderstone.server.api.messages.AdvancedMessage.AdvancedMessagePart;
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
	
	public AdvancedMessage color(ChatColor ... colors)
	{
		return new AdvancedMessage().getBase().setText(text).combineColor(colors).build();
	}

}
