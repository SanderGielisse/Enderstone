package org.enderstone.server.packet.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import io.netty.buffer.ByteBuf;

import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.json.JSONException;
import org.json.JSONObject;

public class PacketInRequest extends Packet {

	// no fields

	@Override
	public void read(ByteBuf buf) throws Exception {
		// none
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	@Override
	public void onRecieve(NetworkManager networkManager) {
		EnderLogger.info("Pinged... by: " + networkManager.latestHandshakePacket.getHostname() + ":" + networkManager.latestHandshakePacket.getPort());

		int protocol = networkManager.latestHandshakePacket.getProtocol();
		if(!Main.PROTOCOL.contains(protocol)){
			protocol = 5;
		}
		
		JSONObject json = new JSONObject();
		json.put("version", new JSONObject().put("name", Main.PROTOCOL_VERSION).put("protocol", protocol));
		json.put("players", new JSONObject().put("max", 20).put("online", Main.getInstance().onlinePlayers.size()));
		json.put("description", Main.getInstance().prop.get("motd"));
		
		if(Main.FAVICON != null){
			json.put("favicon", Main.FAVICON);
		}

		networkManager.sendPacket(new PacketOutResponse(json.toString()));
	}
}
