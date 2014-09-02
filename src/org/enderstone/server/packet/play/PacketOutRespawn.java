package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Main;
import org.enderstone.server.inventory.Inventory.InventoryType;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.BlockId;

public class PacketOutRespawn extends Packet {

	private int dimension;
	private byte difficulty;
	private byte gamemode;
	private String levelType;

	public PacketOutRespawn(int dimension, byte difficulty, byte gamemode, String levelType) {
		this.dimension = dimension;
		this.difficulty = difficulty;
		this.gamemode = gamemode;
		this.levelType = levelType;
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		buf.writeInt(dimension);
		buf.writeByte(difficulty);
		buf.writeByte(gamemode);
		writeString(levelType, buf);
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 2 + getStringSize(levelType) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x07;
	}
	
	@Override
	public void onSend(final NetworkManager networkManager) {
		if(!Thread.currentThread().equals(Main.getInstance().mainThread)){
			Main.getInstance().sendToMainThread(new Runnable() {
				
				@Override
				public void run() {
					//refillInventory(networkManager);
				}
			});
			return;
		}
		//refillInventory(networkManager);
	}
	
	public void refillInventory(NetworkManager networkManager){
		networkManager.player.inventory.setItem(InventoryType.HOTBAR, 5, new ItemStack(BlockId.DIRT.getId(), (byte) 101, (short) 1));
	}
}
