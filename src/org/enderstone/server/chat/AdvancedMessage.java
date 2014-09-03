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
package org.enderstone.server.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Fernando
 */
public final class AdvancedMessage implements Message, AdvancedMessagePart {

	public AdvancedMessage() {
	}
	
	public AdvancedMessage(String firstMessageText) {
		this();
		this.getLatest().setText(firstMessageText);
	}

	protected final List<MessagePart> messageParts = new ArrayList<>();
	private MessagePart working = null;
	int index = -1;

	private MessagePart getLatest() {
		if (working == null && index == -1) {
			return (MessagePart) addPart("", Type.PLAIN);
		}
		return working;
	}

	@Override
	public String toPlainText() {
		MessagePart head;
		if(index == -1)
		{
			head = (MessagePart) addPart("", Type.PLAIN);
		}
		else
		{
			head = messageParts.get(0);
		}
		Iterator<MessagePart> parts = messageParts.listIterator(1);
		StringBuilder b = new StringBuilder();
		head.buildPlain(parts, b);
		return b.toString();
	}

	@Override
	public String toMessageJson() {
		MessagePart head;
		if(index == -1)
		{
			head = (MessagePart) addPart("", Type.PLAIN);
		}
		else
		{
			head = messageParts.get(0);
		}
		Iterator<MessagePart> parts = messageParts.listIterator(1);
		return head.buildMessage(parts).toString();
	}

	@Override
	public String toAsciiText() {
		MessagePart head;
		if(index == -1)
		{
			head = (MessagePart) addPart("", Type.PLAIN);
		}
		else
		{
			head = messageParts.get(0);
		}
		Iterator<MessagePart> parts = messageParts.listIterator(1);
		StringBuilder b = new StringBuilder();
		head.buildAscii(parts, b);
		return b.toString();
	}

	@Override
	public AdvancedMessagePart addPart(String text) {
		return this.addPart(text, Type.PLAIN);
	}

	@Override
	public AdvancedMessagePart addPart(String text, Type type) {
		messageParts.add(working = new MessagePart(text, type));
		index++;
		return working;
	}

	@Override
	public AdvancedMessage build() {
		return this;
	}

	@Override
	public AdvancedMessagePart setObjective(String objective) {
		return getLatest().setObjective(objective);
	}

	@Override
	public String getObjective() {
		return getLatest().getObjective();
	}

	@Override
	public AdvancedMessagePart setText(String text) {
		return getLatest().setText(text);
	}

	@Override
	public String getText() {
		return getLatest().getText();
	}

	@Override
	public AdvancedMessagePart setColor(ChatColor color) {
		return getLatest().setColor(color);
	}

	@Override
	public AdvancedMessagePart combineColor(ChatColor... colors) {
		return getLatest().combineColor(colors);
	}

	@Override
	public ChatColor getColor() {
		return getLatest().getColor();
	}

	@Override
	public AdvancedMessagePart setBold(boolean bold) {
		return getLatest().setBold(bold);
	}

	@Override
	public boolean getBold() {
		return getLatest().getBold();
	}

	@Override
	public AdvancedMessagePart setItalic(boolean italic) {
		return getLatest().setItalic(italic);
	}

	@Override
	public boolean getItalic() {
		return getLatest().getItalic();
	}

	@Override
	public AdvancedMessagePart setUnderline(boolean underline) {
		return getLatest().setUnderline(underline);
	}

	@Override
	public boolean getUnderline() {
		return getLatest().getUnderline();
	}

	@Override
	public AdvancedMessagePart setObfuscated(boolean obfuscated) {
		return getLatest().setObfuscated(obfuscated);
	}

	@Override
	public boolean getObfuscated() {
		return getLatest().getObfuscated();
	}

	@Override
	public AdvancedMessagePart setClickEvent(ClickEventType type, String value) {
		return getLatest().setClickEvent(type, value);
	}

	@Override
	public ClickEventType getClickEventType() {
		return getLatest().getClickEventType();
	}

	@Override
	public String getClickEventValue() {
		return getLatest().getClickEventValue();
	}

	@Override
	public AdvancedMessagePart setHoverEvent(HoverEventType type, String value) {
		return getLatest().setHoverEvent(type, value);
	}

	@Override
	public HoverEventType getHoverEventType() {
		return getLatest().getHoverEventType();
	}

	@Override
	public String getHoverEventValue() {
		return getLatest().getHoverEventValue();
	}

	@Override
	public int getPartIndex() {
		return this.index < 0 ? 0 : index;
	}

	@Override
	public AdvancedMessagePart setType(Type type) {
		return getLatest().setType(type);
	}

	@Override
	public Type getType() {
		return getLatest().getType();
	}

	@Override
	public AdvancedMessagePart setStrikethrough(boolean strikethrough) {
		return getLatest().setStrikethrough(strikethrough);
	}

	@Override
	public boolean getStrikethrough() {
		return getLatest().getStrikethrough();
	}

	@Override
	public AdvancedMessagePart setTranslatingWith(String[] parts) {
		return getLatest().setTranslatingWith(parts);
	}

	@Override
	public String[] getTranslatingWith() {
		return getLatest().getTranslatingWith();
	}

	private class MessagePart implements AdvancedMessagePart {

		private String objective;
		private String text;
		private Type type;
		private boolean bold;
		private boolean italic;
		private boolean underline;
		private boolean obfuscated;
		private boolean strikethrough;
		private ClickEventType clickEvent;
		private String clickEventValue;
		private HoverEventType hoverEvent;
		private String hoverEventValue;
		private ChatColor color;
		private String[] with;

		public MessagePart(String text, Type type) {
			this.text = text;
			this.type = type;
		}

		@Override
		public AdvancedMessagePart addPart(String text) {
			return AdvancedMessage.this.addPart(text);
		}

		@Override
		public AdvancedMessagePart addPart(String text, Type type) {
			return AdvancedMessage.this.addPart(text, type);
		}

		@Override
		public AdvancedMessagePart setObjective(String objective) {
			this.objective = objective;
			return this;
		}

		@Override
		public String getObjective() {
			return this.objective;
		}

		@Override
		public AdvancedMessagePart setText(String text) {
			this.text = text;
			return this;
		}

		@Override
		public String getText() {
			return this.text;
		}

		@Override
		public AdvancedMessagePart setColor(ChatColor color) {
			switch (color) {
				case BLACK:
				case DARK_BLUE:
				case DARK_GREEN:
				case DARK_AQUA:

				case DARK_RED:
				case DARK_PURPLE:
				case GOLD:
				case GRAY:

				case DARK_GRAY:
				case BLUE:
				case GREEN:
				case AQUA:

				case RED:
				case LIGHT_PURPLE:
				case YELLOW:
				case WHITE: {
					this.color = color;
				}
				return this;
				default: {
					throw new IllegalArgumentException("This method only accepts colors,"
							+ " however a non-color chatcolor was pased in: " + color.name());
				}
			}
		}

		@Override
		public AdvancedMessagePart combineColor(ChatColor... colors) {
			for (ChatColor c : colors) {
				switch (c) {
					case BLACK:
					case DARK_BLUE:
					case DARK_GREEN:
					case DARK_AQUA:

					case DARK_RED:
					case DARK_PURPLE:
					case GOLD:
					case GRAY:

					case DARK_GRAY:
					case BLUE:
					case GREEN:
					case AQUA:

					case RED:
					case LIGHT_PURPLE:
					case YELLOW:
					case WHITE: {
						this.color = c;

					}
					break;
					case BOLD: {
						this.bold = true;
					}
					break;
					case STRIKETHROUGH: {
						this.strikethrough = true;
					}
					break;
					case UNDERLINE: {
						this.underline = true;
					}
					break;
					case ITALIC: {
						this.italic = true;
					}
					break;
					case RESET: {
						this.bold = false;
						this.italic = false;
						this.obfuscated = false;
						this.color = null;
						this.strikethrough = false;
						this.underline = false;
						this.color = c;
					}
					break;
					default: {
						throw new IllegalArgumentException("Unknown chatcolor: " + color.name());
					}
				}
			}
			return this;
		}

		@Override
		public ChatColor getColor() {
			return this.color;
		}

		@Override
		public AdvancedMessagePart setBold(boolean bold) {
			this.bold = bold;
			return this;
		}

		@Override
		public boolean getBold() {
			return bold;
		}

		@Override
		public AdvancedMessagePart setItalic(boolean italic) {
			this.italic = italic;
			return this;
		}

		@Override
		public boolean getItalic() {
			return this.italic;
		}

		@Override
		public AdvancedMessagePart setUnderline(boolean underline) {
			this.underline = underline;
			return this;
		}

		@Override
		public boolean getUnderline() {
			return this.underline;
		}

		@Override
		public AdvancedMessagePart setObfuscated(boolean obfuscated) {
			this.obfuscated = obfuscated;
			return this;
		}

		@Override
		public boolean getObfuscated() {
			return obfuscated;
		}

		@Override
		public AdvancedMessagePart setClickEvent(ClickEventType type, String value) {
			this.clickEvent = type;
			this.clickEventValue = value;
			return this;
		}

		@Override
		public ClickEventType getClickEventType() {
			return clickEvent;
		}

		@Override
		public String getClickEventValue() {
			return clickEventValue;
		}

		@Override
		public AdvancedMessagePart setHoverEvent(HoverEventType type, String value) {
			this.hoverEvent = type;
			this.hoverEventValue = value;
			return this;
		}

		@Override
		public HoverEventType getHoverEventType() {
			return hoverEvent;
		}

		@Override
		public String getHoverEventValue() {
			return hoverEventValue;
		}

		@Override
		public AdvancedMessage build() {
			return AdvancedMessage.this;
		}

		@Override
		public int getPartIndex() {
			return AdvancedMessage.this.getPartIndex();
		}

		@Override
		public AdvancedMessagePart setType(Type type) {
			this.type = type;
			return this;
		}

		@Override
		public Type getType() {
			return type;
		}

		@Override
		public AdvancedMessagePart setStrikethrough(boolean strikethrough) {
			this.strikethrough = strikethrough;
			return this;
		}

		@Override
		public boolean getStrikethrough() {
			return this.strikethrough;
		}

		private void buildPlain(Iterator<MessagePart> with, StringBuilder build) {
			build.append(text);
			while (with.hasNext()) {
				with.next().buildPlain(NULL_ITERATOR, build);
			}
		}

		private void buildAscii(Iterator<MessagePart> with, StringBuilder build) {
			this.buildPlain(with, build);
		}

		private JSONObject buildMessage(Iterator<MessagePart> with) {
			JSONObject cache = new JSONObject();
			switch (type) {
				case PLAIN:
					cache.put("text", text);
					break;
				case TRANSLATE:
					cache.put("translate", text);
					JSONArray array = new JSONArray();
					for (String message : this.getTranslatingWith()) {
						array.put(message);
					}
					cache.put("with", array);
					break;
				case SCORE:
					JSONObject tmp = new JSONObject();
					tmp.put("name", text);
					tmp.put("objective", objective);
					cache.put("score", tmp);
					break;
				default:
					throw new IllegalStateException("Invalid message type was found: " + type);
			}
			if (bold)
				cache.put("bold", true);
			if (italic)
				cache.put("italic", true);
			if (underline)
				cache.put("underlined", true);
			if (strikethrough)
				cache.put("strikethrough", true);
			if (obfuscated)
				cache.put("obfuscated", true);
			if (color != null)
				cache.put("color", color.name().toLowerCase(Locale.ENGLISH));
			if (clickEvent != null) {
				JSONObject tmp = new JSONObject();
				tmp.put("action", clickEvent.name().toLowerCase(Locale.ENGLISH));
				tmp.put("value", clickEventValue);
				cache.put("clickEvent", tmp);
			}
			if (hoverEvent != null) {
				JSONObject tmp = new JSONObject();
				tmp.put("action", hoverEvent.name().toLowerCase(Locale.ENGLISH));
				tmp.put("value", hoverEventValue);
				cache.put("hoverEvent", tmp);
			}
			if (with.hasNext()) {
				JSONArray tmp = new JSONArray();
				do {
					tmp.put(with.next().buildMessage(NULL_ITERATOR));
				} while (with.hasNext());
				cache.put("extra", tmp);
			}
			return cache;
		}

		@Override
		public AdvancedMessagePart setTranslatingWith(String[] parts) {
			this.with = parts;
			return this;
		}

		@Override
		public String[] getTranslatingWith() {
			return this.with;
		}
	}
	private static final Iterator<MessagePart> NULL_ITERATOR = new Iterator<MessagePart>() {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public MessagePart next() {
			throw new UnsupportedOperationException("I am sorry, but this iterator is empty...");
		}

		@Override
		public void remove() {}
	};
}
