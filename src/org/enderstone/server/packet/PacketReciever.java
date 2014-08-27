package me.bigteddy98.mcserver.packet;

import java.util.UUID;

import me.bigteddy98.mcserver.Location;
import me.bigteddy98.mcserver.Main;
import me.bigteddy98.mcserver.entity.EnderPlayer;
import me.bigteddy98.mcserver.packet.login.PacketInLoginStart;
import me.bigteddy98.mcserver.packet.login.PacketOutLoginSucces;
import me.bigteddy98.mcserver.packet.play.PacketInAnimation;
import me.bigteddy98.mcserver.packet.play.PacketInChatMessage;
import me.bigteddy98.mcserver.packet.play.PacketInClientSettings;
import me.bigteddy98.mcserver.packet.play.PacketInEntityAction;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerLook;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerOnGround;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerPosition;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerPositionLook;
import me.bigteddy98.mcserver.packet.play.PacketInPluginMessage;
import me.bigteddy98.mcserver.packet.play.PacketKeepAlive;
import me.bigteddy98.mcserver.packet.play.PacketOutJoinGame;
import me.bigteddy98.mcserver.packet.play.PacketOutPlayerAbilities;
import me.bigteddy98.mcserver.packet.play.PacketOutPlayerPositionLook;
import me.bigteddy98.mcserver.packet.play.PacketOutPluginMessage;
import me.bigteddy98.mcserver.packet.play.PacketOutSpawnPosition;
import me.bigteddy98.mcserver.packet.status.PacketInRequest;
import me.bigteddy98.mcserver.packet.status.PacketOutResponse;
import me.bigteddy98.mcserver.packet.status.PacketPing;

import org.json.JSONObject;

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

	public void packetInPlayerPosition(PacketInPlayerPosition packet) {
		Location loc = this.networkManager.player.getLocation();
		loc.setX(packet.getX());
		loc.setY(packet.getFeetY());
		loc.setZ(packet.getZ());
	}

	public void packetInPlayerPositionLook(PacketInPlayerPositionLook packet) {
		Location loc = this.networkManager.player.getLocation();
		loc.setX(packet.getX());
		loc.setY(packet.getFeetY());
		loc.setZ(packet.getZ());
		loc.setPitch(packet.getPitch());
		loc.setYaw(packet.getYaw());
	}

	public void packetInAnimation(PacketInPluginMessage packet) {

	}

	public void packetInRequest(PacketInRequest packet) {
		// String jsonResponse = "{\"version\": {\"name\": \"" +
		// Main.PROTOCOL_VERSION + "\",\"protocol\": " + Main.PROTOCOL +
		// "},\"players\": {\"max\": 100,\"online\": 5,\"sample\":[{\"name\":\"sander2798\", \"id\": "
		// + UUID.randomUUID().toString() +
		// "\"\"}]},	\"description\": {\"text\":\"Hello world\"}}";

		JSONObject json = new JSONObject();
		json.put("version", new JSONObject().put("name", Main.PROTOCOL_VERSION).put("protocol", Main.PROTOCOL));
		json.put("players", new JSONObject().put("max", 10).put("online", Main.getInstance().onlinePlayers.size() + 3));
		json.put("description", "It seems like you are using the Enderstone Server");

		this.networkManager.sendPacket(new PacketOutResponse(json.toString()));
	}

	public void packetPing(PacketPing packet) {
		this.networkManager.sendPacket(packet);
	}

	public void packetInLoginStart(PacketInLoginStart packet) throws Exception {
		if (this.networkManager.player == null) {

			this.networkManager.player = new EnderPlayer(packet.getPlayerName(), this.networkManager, UUID.randomUUID().toString());
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					Main.getInstance().onlinePlayers.add(networkManager.player);
					networkManager.player.onJoin();
				}
			});
		}

		this.networkManager.sendPacket(new PacketOutLoginSucces(this.networkManager.player.uuid, this.networkManager.player.getPlayerName()));
		this.networkManager.handShakeStatus = 3;
		this.networkManager.sendPacket(new PacketOutJoinGame(this.networkManager.player.getEntityId(), (byte) 1, (byte) 0, (byte) 1, (byte) 60, "default"));

		this.networkManager.player.getLocation().setX(0);
		this.networkManager.player.getLocation().setY(100);
		this.networkManager.player.getLocation().setZ(0);

		Main.getInstance().mainWorld.doChunkUpdatesForPlayer(this.networkManager.player, this.networkManager.player.chunkInformer, 3);

		this.networkManager.sendPacket(new PacketOutSpawnPosition(0, 100, 0));

		int i = 0;

		if (networkManager.player.isCreative)
			i = (byte) (i | 0x1);
		if (networkManager.player.isFlying)
			i = (byte) (i | 0x2);
		if (networkManager.player.canFly)
			i = (byte) (i | 0x4);
		if (networkManager.player.godMode)
			i = (byte) (i | 0x8);

		this.networkManager.sendPacket(new PacketOutPlayerAbilities((byte) i, 0.1F, 0.1F));
		this.networkManager.sendPacket(new PacketOutPlayerPositionLook(0, 100, 0, 0F, 0F, false));
	}

	public void packetInPluginMessage(PacketInPluginMessage packet) {
		if (packet.getChannel().equals("REGISTER")) {
			// REGISTER.add(new String(message.getData(), "UTF-8"));
		} else if (packet.getChannel().equals("UNREGISTER")) {
			// REGISTER.remove(new String(message.getData(), "UTF-8"));
		} else if (packet.getChannel().equals("MC|Brand")) {
			this.networkManager.sendPacket(new PacketOutPluginMessage(packet.getChannel(), packet.getLength(), packet.getData()));
		}
	}

	public void packetKeepAlive(PacketKeepAlive packet) {
		// TODO Auto-generated method stub

	}

	public void packetInChatMessage(PacketInChatMessage packet) {
		this.networkManager.player.sendChatMessage(packet.getMessage());
	}
}
