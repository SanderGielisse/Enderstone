package org.enderstone.server.commands;

import org.enderstone.server.chat.Message;

/**
 *
 * @author Fernando
 */
public interface CommandSender {
	public boolean isOnline();
	public boolean sendMessage(Message message);
	public boolean sendRawMessage(Message message);
	public String getName();
}
