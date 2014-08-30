package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
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

	public PacketOutEntityStatus() {
	}
	

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeInt(entityId);
		buf.writeByte(entityStatus.getStatus());
	}

	@Override
	public int getSize() throws Exception {
		int size = 0;
		size += 4;
		size += 1;
		return size + getVarIntSize(getId());
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
