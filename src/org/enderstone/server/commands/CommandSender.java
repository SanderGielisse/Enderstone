/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
