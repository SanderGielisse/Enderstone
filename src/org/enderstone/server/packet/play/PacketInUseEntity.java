package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInUseEntity extends Packet {

	private int targetId;
	private int mouseClick;
	private float targetX;
	private float targetY;
	private float targetZ;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.targetId = readVarInt(buf);
		this.mouseClick = readVarInt(buf);
		if (mouseClick == 2) {
			this.targetX = buf.readFloat();
			this.targetY = buf.readFloat();
			this.targetZ = buf.readFloat();
		}
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(targetId) + getVarIntSize(mouseClick) + ((mouseClick == 2) ? (getFloatSize() * 3) : 0) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x02;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				if (mouseClick == 1) { // left click
					EnderPlayer player = Main.getInstance().getPlayer(targetId);
					if (player == null) {
						networkManager.sendPacket(new PacketOutPlayerDisconnect("Invalid target id, probably a server bug, please report!"));
						return;
					}
					if (player.isDead()) {
						return;
					}
					player.onLeftClick(networkManager.player);
				} else if (mouseClick == 0) { // right click
					EnderPlayer player = Main.getInstance().getPlayer(targetId);
					if (player == null) {
						networkManager.sendPacket(new PacketOutPlayerDisconnect("Invalid target id, probably a server bug, please report!"));
						return;
					}
					if (player.isDead()) {
						return;
					}
					player.onRightClick(networkManager.player);
				}
			}
		});
	}
}
