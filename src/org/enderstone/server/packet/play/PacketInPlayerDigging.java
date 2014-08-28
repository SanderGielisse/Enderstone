package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;

import org.enderstone.server.Main;
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

	public void onRecieve(org.enderstone.server.packet.NetworkManager networkManager) throws Exception {
		//TODO not working properly
		
		Main.getInstance().sendToMainThread(new Runnable() {
			
			@Override
			public void run() {
				EnderChunk chunk = Main.getInstance().mainWorld.getOrCreateChunk(getX() >> 4, getZ() >> 4);
				try {
					chunk.setBlock(getX() & 0xF, getY() & 0xFF, getZ() & 0xF, BlockId.AIR, (byte) 0);
				} catch (Exception e) {
					e.printStackTrace();
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
