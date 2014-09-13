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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import org.enderstone.server.Main;
import org.enderstone.server.entity.PlayerTextureStore;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;
import org.enderstone.server.uuid.UUIDFactory;

public class PacketInLoginStart extends Packet {

	// incoming
	private String name;

	public PacketInLoginStart() {
	}

	public PacketInLoginStart(String name) {
		this.name = name;
	}

	@Override
	public void read(PacketDataWrapper wrapper) throws IOException {
		this.name = wrapper.readString();
	}

	@Override
	public void write(PacketDataWrapper wrapper) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(name) + getVarIntSize(getId());
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		assert networkManager.player == null;
		
		networkManager.wantedName = name;
		
		try {
			ByteBuf tempBuf = Unpooled.buffer();
			new PacketOutSetCompression(1).writeFully(new PacketDataWrapper(networkManager, tempBuf));
			networkManager.ctx.pipeline().channel().writeAndFlush(tempBuf);
			networkManager.enableCompression();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (Main.getInstance().onlineMode) {
			networkManager.regenerateEncryptionSettings();
			NetworkManager.EncryptionSettings en = networkManager.getEncryptionSettings();
			networkManager.sendPacket(new PacketOutEncryptionRequest(
					en.getServerid(), en.getKeyPair().getPublic(), en.getVerifyToken()));
		} else {
			UUIDFactory factory = Main.getInstance().uuidFactory;
			UUID uuid = factory.getPlayerUUIDAsync(name);
			PlayerTextureStore texture;
			if(uuid == null)
			{
				texture = PlayerTextureStore.DEFAULT_STORE;
				uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charset.forName("UTF_8")));
			}
			else
			{
				texture = factory.getTextureDataAsync(uuid);
			}
			networkManager.wantedName = name;
			networkManager.uuid = uuid;
			networkManager.skinBlob = texture;
			networkManager.spawnPlayer();
		}
	}

	@Override
	public byte getId() {
		return 0x00;
	}

	public String getPlayerName() {
		return name;
	}
}
