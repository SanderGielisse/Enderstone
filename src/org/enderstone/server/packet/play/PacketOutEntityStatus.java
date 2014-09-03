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

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.packet.Packet;

/**
 *
 * @author ferrybig
 */
public class PacketOutEntityStatus extends Packet {

	private int entityId;
	private Status entityStatus;

	public PacketOutEntityStatus(int entityId, Status entityStatus) {
		this.entityId = entityId;
		this.entityStatus = entityStatus;
	}	

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(entityId);
		buf.writeByte(entityStatus.getStatus());
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x1A;
	}

	public enum Status {

		LIVING_ENTITY_UNKNOWN(0),
		PLAYER_ENTITY_UNKNOWN(1),
		LIVING_ENTITY_HURT(2),
		LIVING_ENTITY_DEAD(3),
		IRON_GOLEM_ARMS(4),
		WOLF_TAME_HEART(6),
		WOLF_TAME_SMOKE(7),
		WOLF_SHAKE(8),
		EAT_ACCEPT(9),
		SHEEP_EAT(10),
		IRON_GOLEM_ROSE(11),
		VILLAGER_MATING(12),
		VILLAGER_REVENGE(13),
		VILLAGER_HAPPY(14),
		WITCH_ANNIMATION(15),
		ZOMBIE_TRANSFORMATION(16),
		FIREWORK_EXPLODE(17),
		ANIMAL_IN_LOVE(18),;
		private final byte status;

		public byte getStatus() {
			return status;
		}

		private Status(int status) {
			this.status = (byte) status;
		}

	}
}
