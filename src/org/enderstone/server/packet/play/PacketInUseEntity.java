package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInUseEntity extends Packet {

	private int targetId;
	private byte mouseClick;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.targetId = buf.readInt();
		this.mouseClick = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getIntSize() + 1 + getVarIntSize(getId());
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
				if (getMouseClick() == 1) { // left click
					EnderPlayer player = Main.getInstance().getPlayer(getTargetId());
					if (player == null) {
						networkManager.sendPacket(new PacketOutPlayerDisconnect("Invalid target id, probably a server bug, please report!"));
						return;
					}
					if (player.isDead()) {
						return;
					}
					player.onLeftClick(networkManager.player);
				} else if (getMouseClick() == 0) { // right click
					EnderPlayer player = Main.getInstance().getPlayer(getTargetId());
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

	public int getTargetId() {
		return targetId;
	}

	public byte getMouseClick() {
		return mouseClick;
	}
}
