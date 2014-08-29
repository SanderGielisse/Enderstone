package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.packet.Packet;

public class PacketOutUpdateHealth extends Packet {

	private float health;
	private short food;
	private float foodSaturation;

	public PacketOutUpdateHealth(float health, short food, float foodSaturation) {
		this.health = health;
		this.food = food;
		this.foodSaturation = foodSaturation;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		buf.writeFloat(health);
		buf.writeShort(food);
		buf.writeFloat(foodSaturation);
	}

	@Override
	public int getSize() throws Exception {
		return (getFloatSize() * 2) + getShortSize() + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x06;
	}
}
