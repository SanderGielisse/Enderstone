package me.bigteddy98.mcserver.packet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.bigteddy98.mcserver.packet.login.PacketInLoginStart;
import me.bigteddy98.mcserver.packet.play.PacketInAnimation;
import me.bigteddy98.mcserver.packet.play.PacketInClientSettings;
import me.bigteddy98.mcserver.packet.play.PacketInEntityAction;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerLook;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerOnGround;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerPosition;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerPositionLook;
import me.bigteddy98.mcserver.packet.play.PacketInPluginMessage;
import me.bigteddy98.mcserver.packet.play.PacketKeepAlive;
import me.bigteddy98.mcserver.packet.play.PacketOutPlayerDisconnect;
import me.bigteddy98.mcserver.packet.status.PacketPing;
import me.bigteddy98.mcserver.packet.status.PacketInRequest;

public class PacketManager {

	public static final Class<? extends Packet> handshake = PacketHandshake.class;

	public static final Map<Integer, Class<? extends Packet>> status = new HashMap<>();
	public static final Map<Integer, Class<? extends Packet>> login = new HashMap<>();
	public static final Map<Integer, Class<? extends Packet>> play = new HashMap<>();

	static {
		status.put(0x00, PacketInRequest.class);
		status.put(0x01, PacketPing.class);

		login.put(0x00, PacketInLoginStart.class);

		play.put(0x00, PacketKeepAlive.class);
		play.put(0x03, PacketInPlayerOnGround.class);
		play.put(0x04, PacketInPlayerPosition.class);
		play.put(0x05, PacketInPlayerLook.class);
		play.put(0x06, PacketInPlayerPositionLook.class);
		play.put(0x15, PacketInClientSettings.class);
		play.put(0x17, PacketInPluginMessage.class);

		play.put(0x0A, PacketInAnimation.class);
		play.put(0x0B, PacketInEntityAction.class);
	}

	public static Class<? extends Packet> getPacket(NetworkManager manager, int id, HandshakeState handshake) {

		Map<Integer, Class<? extends Packet>> map = null;

		if (handshake == HandshakeState.STATUS) {
			map = status;
		} else if (handshake == HandshakeState.LOGIN) {
			map = login;
		} else if (handshake == HandshakeState.PLAY) {
			map = play;
		}

		for (Entry<Integer, Class<? extends Packet>> s : map.entrySet()) {
			if (s.getKey() == id) {
				return s.getValue();
			}
		}
		manager.sendPacket(new PacketOutPlayerDisconnect(JSONStringBuilder.build("Packet ID 0x" + Integer.toHexString(id) + " " + handshake.toString() + " does not have a valid packet.")));
		throw new RuntimeException("Packet ID 0x" + Integer.toHexString(id) + " " + handshake.toString() + " does not have a valid packet.");
	}
}
