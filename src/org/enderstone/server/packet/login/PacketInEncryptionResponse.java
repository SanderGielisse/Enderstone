/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.enderstone.server.packet.login;

import java.io.IOException;
import java.math.BigInteger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.entity.PlayerTextureStore;
import org.enderstone.server.packet.NetworkEncrypter;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;
import org.enderstone.server.uuid.ServerRequest;
import static org.enderstone.server.uuid.UUIDFactory.parseUUID;
import org.json.JSONObject;

/**
 *
 * @author Fernando
 */
public class PacketInEncryptionResponse extends Packet {

	private byte[] sharedSecret;
	private byte[] verifyToken;

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.sharedSecret = new byte[wrapper.readVarInt()];
		wrapper.readBytes(this.sharedSecret);
		this.verifyToken = new byte[wrapper.readVarInt()];
		wrapper.readBytes(this.verifyToken);
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getSize() throws IOException {
		return getVarIntSize(sharedSecret.length) + getVarIntSize(verifyToken.length) + sharedSecret.length + verifyToken.length + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		NetworkManager.EncryptionSettings ec = networkManager.getEncryptionSettings();
		this.verifyToken = NetworkEncrypter.decrypt(ec.getKeyPair().getPrivate(), this.verifyToken);
		this.sharedSecret = NetworkEncrypter.decrypt(ec.getKeyPair().getPrivate(), this.sharedSecret);
		SecretKey key = new SecretKeySpec(sharedSecret, "AES");
		try {
			String hash = new BigInteger(NetworkEncrypter.createHash(ec.getServerid(), ec.getKeyPair().getPublic(), key)).toString(16);
			String url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + networkManager.wantedName + "&serverId=" + hash;
			ServerRequest r = new ServerRequest(url);
			JSONObject json = r.get();
			if (json == null) {
				networkManager.disconnect("invalid encryption token", false);
				return;
			}
			String uuid = json.optString("id", null);
			networkManager.wantedName = json.optString("name", networkManager.wantedName);
			if (uuid == null) {
				networkManager.disconnect("invalid session server response: \n" + url + "\n" + json.toString(), false);
				return;
			}
			networkManager.uuid = parseUUID(uuid);
			networkManager.skinBlob = new PlayerTextureStore(json.optJSONArray("properties"));
			networkManager.setupEncryption(key);
			networkManager.spawnPlayer();
			networkManager.player.updateClientSettings();
		} catch (IOException ex) {
			networkManager.disconnect("internal exception: " + ex.toString(), false);
			EnderLogger.exception(ex);
		}
	}
}
