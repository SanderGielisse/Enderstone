package me.bigteddy98.mcserver.packet;

import java.util.UUID;

import me.bigteddy98.mcserver.Main;
import me.bigteddy98.mcserver.entity.EnderPlayer;
import me.bigteddy98.mcserver.packet.login.PacketInLoginStart;
import me.bigteddy98.mcserver.packet.login.PacketOutLoginSucces;
import me.bigteddy98.mcserver.packet.play.PacketInAnimation;
import me.bigteddy98.mcserver.packet.play.PacketInClientSettings;
import me.bigteddy98.mcserver.packet.play.PacketInEntityAction;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerLook;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerOnGround;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerPosition;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerPositionLook;
import me.bigteddy98.mcserver.packet.play.PacketInPluginMessage;
import me.bigteddy98.mcserver.packet.play.PacketOutJoinGame;
import me.bigteddy98.mcserver.packet.play.PacketOutPlayerAbilities;
import me.bigteddy98.mcserver.packet.play.PacketOutPlayerPositionLook;
import me.bigteddy98.mcserver.packet.play.PacketOutSpawnPosition;
import me.bigteddy98.mcserver.packet.status.PacketInRequest;
import me.bigteddy98.mcserver.packet.status.PacketOutResponse;
import me.bigteddy98.mcserver.packet.status.PacketPing;

public class PacketReciever {

	private final NetworkManager networkManager;

	public PacketReciever(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public void packetInAnimation(PacketInAnimation packet) {

	}

	public void packetInClientSettings(PacketInClientSettings packet) {

	}

	public void packetInEntityAction(PacketInEntityAction packet) {

	}

	public void packetInPlayerLook(PacketInPlayerLook packet) {

	}

	public void PacketInPlayerOnGround(PacketInPlayerOnGround packet) {

	}

	public void packetInAnimation(PacketInPlayerPosition packet) {

	}

	public void packetInAnimation(PacketInPlayerPositionLook packet) {

	}

	public void packetInAnimation(PacketInPluginMessage packet) {

	}

	public void packetInRequest(PacketInRequest packet) {
		String jsonResponse = "{\"version\": {\"name\": \"" + Main.PROTOCOL_VERSION + "\",\"protocol\": " + Main.PROTOCOL + "},\"players\": {\"max\": 100,\"online\": 5,\"sample\":[{\"name\":\"sander2798\", \"id\": 6743a814-9d41-4d33-af9e-e143bc2d462c\"\"}]},	\"description\": {\"text\":\"Hello world\"}}";
		this.networkManager.sendPacket(new PacketOutResponse(jsonResponse));
	}

	public void packetPing(PacketPing packet) {
		this.networkManager.sendPacket(packet);
	}

	public void packetInLoginStart(PacketInLoginStart packet) {
		if (this.networkManager.player == null) {
			this.networkManager.player = new EnderPlayer(packet.getPlayerName(), this.networkManager);
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					Main.getInstance().onlinePlayers.add(networkManager.player);
				}
			});
		}

		this.networkManager.sendPacket(new PacketOutLoginSucces(UUID.randomUUID().toString(), this.networkManager.player.getPlayerName()));
		this.networkManager.handShakeStatus = 3;
		this.networkManager.sendPacket(new PacketOutJoinGame(10, (byte) 2, (byte) 0, (byte) 1, (byte) 60, "default"));

		this.networkManager.player.getLocation().setX(0);
		this.networkManager.player.getLocation().setY(30);
		this.networkManager.player.getLocation().setZ(0);

		Main.getInstance().mainWorld.doChunkUpdatesForPlayer(this.networkManager.player, this.networkManager.player.chunkInformer, 3);

		this.networkManager.sendPacket(new PacketOutSpawnPosition(0, 30, 0));
		this.networkManager.sendPacket(new PacketOutPlayerAbilities((byte) 0, 0.1F, 0.1F));
		this.networkManager.sendPacket(new PacketOutPlayerPositionLook(0, 30, 0, 0F, 0F, false));
	}
}
