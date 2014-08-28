package org.enderstone.server.chat;

/**
 *
 * @author ferrybig
 */
public interface Message {

	public String toPlainText();

	public String toMessageJson();

	public String toAsciiText();
}
