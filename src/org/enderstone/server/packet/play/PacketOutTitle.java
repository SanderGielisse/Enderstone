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
package org.enderstone.server.packet.play;

import java.io.IOException;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketOutTitle extends Packet {

	private TitleAction titleAction;

	public PacketOutTitle(TitleAction titleAction) {
		this.titleAction = titleAction;
	}

	@Override
	public void read(PacketDataWrapper buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper buf) throws IOException {
		buf.writeVarInt(titleAction.getId());
		this.titleAction.write(buf);
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(titleAction.getId()) + titleAction.getSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x45;
	}

	public abstract static class TitleAction {
		public abstract int getId();

		public abstract void write(PacketDataWrapper wrapper) throws IOException;

		public abstract int getSize() throws IOException;
	}

	public static class ActionDisplayTitle extends TitleAction {

		private String jsonMessage;
		private Message message;

		public ActionDisplayTitle(Message message) {
			this.message = message;
		}

		@Override
		public int getId() {
			return 0;
		}

		@Override
		public void write(PacketDataWrapper wrapper) {
			if (jsonMessage == null) {
				jsonMessage = message.toMessageJson();
			}
			wrapper.writeString(jsonMessage);
		}

		@Override
		public int getSize() throws IOException {
			if (jsonMessage == null) {
				jsonMessage = message.toMessageJson();
			}
			return Packet.getStringSize(jsonMessage);
		}
	}

	public static class ActionSubtitle extends TitleAction {

		private String jsonMessage;
		private Message message;

		public ActionSubtitle(Message message) {
			this.message = message;
		}

		@Override
		public int getId() {
			return 1;
		}

		@Override
		public void write(PacketDataWrapper wrapper) {
			if (jsonMessage == null) {
				jsonMessage = message.toMessageJson();
			}
			wrapper.writeString(jsonMessage);
		}

		@Override
		public int getSize() throws IOException {
			if (jsonMessage == null) {
				jsonMessage = message.toMessageJson();
			}
			return Packet.getStringSize(jsonMessage);
		}
	}

	public static class ActionTimes extends TitleAction {

		private int fadeIn;
		private int stay;
		private int fadeOut;

		public ActionTimes(int fadeIn, int stay, int fadeOut) {
			this.fadeIn = fadeIn;
			this.stay = stay;
			this.fadeOut = fadeOut;
		}

		@Override
		public int getId() {
			return 2;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
			wrapper.writeInt(fadeIn);
			wrapper.writeInt(stay);
			wrapper.writeInt(fadeOut);
		}

		@Override
		public int getSize() throws IOException {
			return (Packet.getIntSize() * 3);
		}
	}

	public static class ActionClear extends TitleAction {

		@Override
		public int getId() {
			return 3;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
		}

		@Override
		public int getSize() throws IOException {
			return 0;
		}
	}

	public static class ActionReset extends TitleAction {

		@Override
		public int getId() {
			return 3;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
		}

		@Override
		public int getSize() throws IOException {
			return 0;
		}
	}
}
