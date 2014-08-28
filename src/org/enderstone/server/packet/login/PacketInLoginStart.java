package org.enderstone.server.packet.login;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutJoinGame;
import org.enderstone.server.packet.play.PacketOutPlayerAbilities;
import org.enderstone.server.packet.play.PacketOutPlayerPositionLook;
import org.enderstone.server.packet.play.PacketOutSpawnPosition;
import org.enderstone.server.uuid.UUIDFactory;
import org.json.JSONObject;

public class PacketInLoginStart extends Packet {

	// incoming
	private String name;

	public PacketInLoginStart() {
	}

	public PacketInLoginStart(String name) {
		this.name = name;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.name = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(name) + getVarIntSize(getId());
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (networkManager.player == null) {
					UUIDFactory factory = Main.getInstance().uuidFactory;
					UUID uuid = factory.getPlayerUUIDAsync(getPlayerName());
					JSONObject property = factory.getTextureDataAsync(uuid);
					networkManager.player = new EnderPlayer(getPlayerName(), networkManager, uuid.toString(), (property == null) ? null : property.getString("value"), (property == null) ? null : property.getString("signature"));
					Main.getInstance().sendToMainThread(new Runnable() {

						@Override
						public void run() {
							Main.getInstance().onlinePlayers.add(networkManager.player);
							try {
								networkManager.player.onJoin();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}

				networkManager.sendPacket(new PacketOutLoginSucces(networkManager.player.uuid, networkManager.player.getPlayerName()));
				networkManager.handShakeStatus = 3;
				networkManager.sendPacket(new PacketOutJoinGame(networkManager.player.getEntityId(), (byte) 1, (byte) 0, (byte) 1, (byte) 60, "default"));

				networkManager.player.getLocation().setX(0);
				networkManager.player.getLocation().setY(100);
				networkManager.player.getLocation().setZ(0);

				Main.getInstance().mainWorld.doChunkUpdatesForPlayer(networkManager.player, networkManager.player.chunkInformer, 3);

				networkManager.sendPacket(new PacketOutSpawnPosition(0, 100, 0));

				int i = 0;
				if (networkManager.player.isCreative)
					i = (byte) (i | 0x1);
				if (networkManager.player.isFlying)
					i = (byte) (i | 0x2);
				if (networkManager.player.canFly)
					i = (byte) (i | 0x4);
				if (networkManager.player.godMode)
					i = (byte) (i | 0x8);

				networkManager.sendPacket(new PacketOutPlayerAbilities((byte) i, 0.1F, 0.1F));
				networkManager.sendPacket(new PacketOutPlayerPositionLook(0, 100, 0, 0F, 0F, false));
			}
		}).start();
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	public String getPlayerName() {
		return name;
	}
}
