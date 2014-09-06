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
 * @author Fernando
 */
public class CachedMessage implements Message{

	private final Message upper;

	private CachedMessage(Message upper) {
		this.upper = upper;
	}
	
	public static CachedMessage wrap(Message messafe)
	{
		if(messafe instanceof CachedMessage) return (CachedMessage)messafe;
		return new CachedMessage(messafe);
	}
	
	private transient String plainText;
	@Override
	public String toPlainText() {
		if(plainText != null) return plainText;
		return plainText = upper.toPlainText();
	}

	private transient String jsonText;
	@Override
	public String toMessageJson() {
		if(jsonText != null) return jsonText;
		return jsonText = upper.toMessageJson();
	}

	private transient String asciiText;
	@Override
	public String toAsciiText() {
		if(asciiText != null) return asciiText;
		return asciiText = upper.toAsciiText();
	}
	
	public void clearCache()
	{
		this.asciiText = null;
		this.jsonText = null;
		this.plainText = null;
	}
	
}
