package org.enderstone.server.packet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.enderstone.server.packet.login.PacketInLoginStart;
import org.enderstone.server.packet.play.PacketInAbilities;
import org.enderstone.server.packet.play.PacketInAnimation;
import org.enderstone.server.packet.play.PacketInBlockPlacement;
import org.enderstone.server.packet.play.PacketInChatMessage;
import org.enderstone.server.packet.play.PacketInClickWindow;
import org.enderstone.server.packet.play.PacketInClientSettings;
import org.enderstone.server.packet.play.PacketInClientStatus;
import org.enderstone.server.packet.play.PacketInCloseWindow;
import org.enderstone.server.packet.play.PacketInCreativeInventoryAction;
import org.enderstone.server.packet.play.PacketInEntityAction;
import org.enderstone.server.packet.play.PacketInHeldItemChange;
import org.enderstone.server.packet.play.PacketInPlayerDigging;
import org.enderstone.server.packet.play.PacketInPlayerLook;
import org.enderstone.server.packet.play.PacketInPlayerOnGround;
import org.enderstone.server.packet.play.PacketInPlayerPosition;
import org.enderstone.server.packet.play.PacketInPlayerPositionLook;
import org.enderstone.server.packet.play.PacketInPluginMessage;
import org.enderstone.server.packet.play.PacketInTabComplete;
import org.enderstone.server.packet.play.PacketInUseEntity;
import org.enderstone.server.packet.play.PacketKeepAlive;
import org.enderstone.server.packet.play.PacketOutPlayerDisconnect;
import org.enderstone.server.packet.status.PacketInRequest;
import org.enderstone.server.packet.status.PacketPing;

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
		play.put(0x01, PacketInChatMessage.class);
		play.put(0x02, PacketInUseEntity.class);
		play.put(0x03, PacketInPlayerOnGround.class);
		play.put(0x04, PacketInPlayerPosition.class);
		play.put(0x05, PacketInPlayerLook.class);
		play.put(0x06, PacketInPlayerPositionLook.class);
		play.put(0x07, PacketInPlayerDigging.class);
		play.put(0x08, PacketInBlockPlacement.class);
		play.put(0x09, PacketInHeldItemChange.class);
		play.put(0x10, PacketInCreativeInventoryAction.class);
		play.put(0x13, PacketInAbilities.class);
		play.put(0x15, PacketInClientSettings.class);
		play.put(0x16, PacketInClientStatus.class);
		play.put(0x17, PacketInPluginMessage.class);
		play.put(0x14, PacketInTabComplete.class);

		play.put(0x0A, PacketInAnimation.class);
		play.put(0x0B, PacketInEntityAction.class);
		play.put(0x0D, PacketInCloseWindow.class);
		play.put(0x0E, PacketInClickWindow.class);
	}

	public static Class<? extends Packet> getPacket(NetworkManager manager, int id, HandshakeState handshake) {

		Map<Integer, Class<? extends Packet>> map;

		if (handshake == HandshakeState.STATUS) {
			map = status;
		} else if (handshake == HandshakeState.LOGIN) {
			map = login;
		} else if (handshake == HandshakeState.PLAY) {
			map = play;
		} else {
			throw new IllegalArgumentException("I don't know the packet table for the specified handshakestate! "+ handshake);
		}

		Class<? extends Packet> packet = map.get(id);
		if(packet != null)
			return packet;
		manager.sendPacket(new PacketOutPlayerDisconnect(JSONStringBuilder.build("Packet ID 0x" + Integer.toHexString(id) + " " + handshake.toString() + " does not have a valid packet.")));
		throw new RuntimeException("Packet ID 0x" + Integer.toHexString(id) + " " + handshake.toString() + " does not have a valid packet.");
	}
}
