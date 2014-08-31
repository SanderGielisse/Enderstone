package org.enderstone.server.packet.login;

import static org.enderstone.server.uuid.UUIDFactory.parseUUID;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.math.BigInteger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.entity.PlayerTextureStore;
import org.enderstone.server.packet.NetworkEncrypter;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.uuid.ServerRequest;
import org.json.JSONObject;

/**
 *
 * @author Fernando
 */
public class PacketInEncryptionResponse extends Packet {

	byte[] sharedSecret;
	byte[] verifyToken;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.sharedSecret = new byte[buf.readShort()];
		buf.readBytes(this.sharedSecret);
		this.verifyToken = new byte[buf.readShort()];
		buf.readBytes(this.verifyToken);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getSize() throws IOException {
		return 2 + 2 + sharedSecret.length + verifyToken.length + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		NetworkManager.EncryptionSettings ec = networkManager.getEncryptionSettings();
		this.verifyToken = NetworkEncrypter.b(ec.getKeyPair().getPrivate(), this.verifyToken);
		this.sharedSecret = NetworkEncrypter.b(ec.getKeyPair().getPrivate(), this.sharedSecret);
		SecretKey key = new SecretKeySpec(sharedSecret, "AES");
		try {
			String hash = new BigInteger(NetworkEncrypter.a(ec.getServerid(), ec.getKeyPair().getPublic(), key)).toString(16);
			String url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + networkManager.wantedName
					+ "&serverId=" + hash;
			ServerRequest r = new ServerRequest(url);
			JSONObject json = r.get();
			if (json == null) {
				networkManager.disconnect("invalid encryption token");
				return;
			}
			String uuid = json.optString("id", null);
			networkManager.wantedName = json.optString("name", networkManager.wantedName);
			if (uuid == null) {
				networkManager.disconnect("invalid session server response: \n" + url + "\n" + json.toString());
				return;
			}
			networkManager.uuid = parseUUID(uuid);
			networkManager.skinBlob = new PlayerTextureStore(json.optJSONArray("properties"));
			networkManager.setupEncryption(key);
			networkManager.spawnPlayer();
		} catch (IOException ex) {
			networkManager.disconnect("internal exception: " + ex.toString());
			EnderLogger.exception(ex);
		}
	}

}
