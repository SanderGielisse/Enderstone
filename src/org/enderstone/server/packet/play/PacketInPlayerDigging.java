package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.EnderChunk;

public class PacketInPlayerDigging extends Packet {

	private byte status;
	private int x;
	private byte y;
	private int z;
	private byte face;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.status = buf.readByte();
		this.x = buf.readInt();
		this.y = buf.readByte();
		this.z = buf.readInt();
		this.face = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return 3 + (2 * getIntSize()) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x07;
	}

	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				if (getStatus() == 2) {
					Location loc;
					if (networkManager.player.getLocation().isInRange(6, loc = new Location("", getX(), getY(), getZ(), 0F, 0F))) {
						Main.getInstance().mainWorld.setBlockAt(getX(), getY(), getZ(), BlockId.AIR, (byte) 0);
					}
					Main.getInstance().mainWorld.broadcastSound("dig.grass", x, y, z, 1F, (byte) 63, loc, networkManager.player);
				}
			}
		});
	}

	public byte getStatus() {
		return status;
	}

	public int getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public byte getFace() {
		return face;
	}
}
