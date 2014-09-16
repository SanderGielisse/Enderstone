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
import java.util.UUID;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.entity.ProfileProperty;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;

public class PacketOutPlayerListItem extends Packet {

	private int action;
	private int length;
	private Action[] actions;

	public PacketOutPlayerListItem(Action[] actions) {
		this.action = actions[0].getActionId();
		this.length = actions.length;
		this.actions = actions;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		wrapper.writeVarInt(action);
		wrapper.writeVarInt(length);

		for (Action action : actions) {
			action.write(wrapper);
		}
	}

	@Override
	public int getSize() throws IOException {
		int actionsSize = 0;
		for (Action action : actions) {
			actionsSize += action.getSize();
		}
		return actionsSize + getVarIntSize(action) + getVarIntSize(length) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x38;
	}

	public abstract static class Action {
		private UUID uuid;

		public Action(UUID uuid) {
			this.uuid = uuid;
		}

		public UUID getUUID() {
			return uuid;
		}

		public abstract int getActionId();

		public abstract void write(PacketDataWrapper wrapper) throws IOException;

		public abstract int getSize() throws IOException;
	}

	public static class ActionAddPlayer extends Action {

		private String name;
		private ProfileProperty[] properties;
		private int gamemode;
		private int ping;
		private boolean hasDisplayName;
		private String jsonDisplayName;

		public ActionAddPlayer(UUID uuid, String name, ProfileProperty[] properties, int gamemode, int ping, boolean hasDisplayName, String jsonDisplayName) {
			super(uuid);
			this.name = name;
			this.properties = properties;
			this.gamemode = gamemode;
			this.ping = ping;
			this.hasDisplayName = hasDisplayName;
			this.jsonDisplayName = jsonDisplayName;
		}

		@Override
		public int getActionId() {
			return 0;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
			wrapper.writeUUID(getUUID());
			wrapper.writeString(name);
			wrapper.writeVarInt(properties.length);

			for (ProfileProperty prop : properties) {
				wrapper.writeString(prop.getName());
				wrapper.writeString(prop.getValue());
				wrapper.writeBoolean(prop.isSigned());
				if (prop.isSigned()) {
					wrapper.writeString(prop.getSignature());
				}
			}
			wrapper.writeVarInt(gamemode);
			wrapper.writeVarInt(ping);
			wrapper.writeBoolean(hasDisplayName);
			if (hasDisplayName) {
				wrapper.writeString(jsonDisplayName);
			}
		}

		@Override
		public int getSize() throws IOException {
			int profileSize = 0;
			for (ProfileProperty prop : properties) {
				profileSize += getStringSize(prop.getName()) + getStringSize(prop.getValue()) + 1 + (prop.isSigned() ? getStringSize(prop.getSignature()) : 0);
			}
			return getUUIDSize() + getStringSize(name) + getVarIntSize(properties.length) + profileSize + getVarIntSize(gamemode) + getVarIntSize(ping) + 1 + (hasDisplayName ? 1 : 0);
		}
	}

	public static class ActionUpdateGamemode extends Action {

		private int gamemode;

		public ActionUpdateGamemode(UUID uuid, int gamemode) {
			super(uuid);
			this.gamemode = gamemode;
		}

		@Override
		public int getActionId() {
			return 1;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
			wrapper.writeUUID(getUUID());
			wrapper.writeVarInt(gamemode);
		}

		@Override
		public int getSize() throws IOException {
			return getUUIDSize() + getVarIntSize(gamemode);
		}
	}

	public static class ActionUpdateLatency extends Action {

		private int ping;

		public ActionUpdateLatency(UUID uuid, int ping) {
			super(uuid);
			this.ping = ping;
		}

		@Override
		public int getActionId() {
			return 2;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
			wrapper.writeUUID(getUUID());
			wrapper.writeVarInt(ping);
		}

		@Override
		public int getSize() throws IOException {
			return getUUIDSize() + getVarIntSize(ping);
		}
	}

	public static class ActionUpdateDisplayName extends Action {

		private boolean hasDisplayName;
		private String jsonDisplayName;

		public ActionUpdateDisplayName(UUID uuid, boolean hasDisplayName, String jsonDisplayName) {
			super(uuid);
			this.hasDisplayName = hasDisplayName;
			this.jsonDisplayName = jsonDisplayName;
		}

		@Override
		public int getActionId() {
			return 3;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
			wrapper.writeUUID(getUUID());
			wrapper.writeBoolean(hasDisplayName);
			if (hasDisplayName) {
				wrapper.writeString(jsonDisplayName);
			}
		}

		@Override
		public int getSize() throws IOException {
			return getUUIDSize() + 1 + (hasDisplayName ? getStringSize(jsonDisplayName) : 0);
		}
	}

	public static class ActionRemovePlayer extends Action {

		public ActionRemovePlayer(UUID uuid) {
			super(uuid);
		}

		@Override
		public int getActionId() {
			return 4;
		}

		@Override
		public void write(PacketDataWrapper wrapper) throws IOException {
			wrapper.writeUUID(getUUID());
		}

		@Override
		public int getSize() throws IOException {
			return getUUIDSize();
		}
	}
}
