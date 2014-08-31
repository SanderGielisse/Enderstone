package org.enderstone.server.packet.login;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import org.enderstone.server.Main;
import org.enderstone.server.entity.PlayerTextureStore;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
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
	public void read(ByteBuf buf) throws IOException {
		this.name = readString(buf);
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
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
