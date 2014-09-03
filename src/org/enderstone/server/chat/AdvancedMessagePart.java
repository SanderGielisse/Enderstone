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


/**
 *
 * @author ferrybig
 */
public interface AdvancedMessagePart {

	public AdvancedMessagePart addPart(String text);

	public AdvancedMessagePart addPart(String text, Type type);

	public AdvancedMessagePart setType(Type type);

	public Type getType();
	
	public AdvancedMessagePart setTranslatingWith(String[] parts);
	
	public String[] getTranslatingWith();

	public AdvancedMessagePart setObjective(String objective);

	public String getObjective();

	public AdvancedMessagePart setText(String text);

	public String getText();

	/**
	 * This method only acceps colors
	 *
	 * @param color
	 * @return
	 */
	public AdvancedMessagePart setColor(ChatColor color);

	/**
	 * This method translates chat colors to the setbold and other methods
	 *
	 * @param colors
	 * @return
	 */
	public AdvancedMessagePart combineColor(ChatColor... colors);

	public ChatColor getColor();

	public AdvancedMessagePart setBold(boolean bold);

	public boolean getBold();

	public AdvancedMessagePart setItalic(boolean italic);

	public boolean getItalic();

	public AdvancedMessagePart setUnderline(boolean underline);

	public boolean getUnderline();

	public AdvancedMessagePart setObfuscated(boolean obfuscated);

	public boolean getObfuscated();
	
	public AdvancedMessagePart setStrikethrough(boolean strikethrough);
	
	public boolean getStrikethrough();

	public AdvancedMessagePart setClickEvent(ClickEventType type, String value);

	public ClickEventType getClickEventType();

	public String getClickEventValue();

	public AdvancedMessagePart setHoverEvent(HoverEventType type, String value);

	public HoverEventType getHoverEventType();

	public String getHoverEventValue();

	public AdvancedMessage build();

	public enum Type {

		PLAIN, TRANSLATE, SCORE
	}

	public enum ClickEventType {

		OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND,
	}

	public enum HoverEventType {

		SHOW_TEXT, SHOW_ARCHIEVEMENT, SHOW_ITEM,
	}

	public int getPartIndex();

}
