package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Location;
import org.enderstone.server.packet.Packet;

public class PacketOutSoundEffect extends Packet {

	private String soundName;
	private int x;
	private int y;
	private int z;
	private float volume;
	private byte pitch;

	public PacketOutSoundEffect(String soundName, Location loc)
	{
		this(soundName,loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),1f,(byte)63);
	}
	
	public PacketOutSoundEffect(String soundName, int x, int y, int z, float volume, byte pitch) {
		this.soundName = soundName;
		this.x = x * 8;
		this.y = y * 8;
		this.z = z * 8;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		writeString(soundName, buf);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeFloat(volume);
		buf.writeByte(pitch);
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(soundName) + (getIntSize() * 3) + getFloatSize() + 1 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x29;
	}
}
