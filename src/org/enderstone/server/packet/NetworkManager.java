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
package org.enderstone.server.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.chat.Message;
import org.enderstone.server.chat.SimpleMessage;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.GameMode;
import org.enderstone.server.entity.PlayerTextureStore;
import org.enderstone.server.packet.codec.DiscardingReader;
import org.enderstone.server.packet.codec.MinecraftCompressionCodex;
import org.enderstone.server.packet.codec.MinecraftDecompressionCodex;
import org.enderstone.server.packet.codec.MinecraftServerCodex;
import org.enderstone.server.packet.login.PacketOutLoginSucces;
import org.enderstone.server.packet.play.PacketOutJoinGame;
import org.enderstone.server.packet.play.PacketOutPlayerAbilities;
import org.enderstone.server.packet.play.PacketOutPlayerPositionLook;
import org.enderstone.server.packet.play.PacketOutSpawnPosition;
import org.enderstone.server.packet.play.PacketOutUpdateHealth;
import org.enderstone.server.packet.play.PacketOutUpdateTime;
import org.enderstone.server.regions.EnderWorld;

public class NetworkManager extends ChannelHandlerAdapter {

	public ChannelHandlerContext ctx;
	public EnderPlayer player;
	public String wantedName;
	private EncryptionSettings encryptionSettings;
	public UUID uuid;
	public PlayerTextureStore skinBlob;
	public int clientVersion;
	
	public PacketHandshake latestHandshakePacket;
	public volatile int handShakeStatus = -1;

	private final Queue<Packet> packets = new LinkedList<>();
	private volatile boolean isConnected = true;

	public EncryptionSettings getEncryptionSettings() {
		return encryptionSettings;
	}

	public void regenerateEncryptionSettings() {
		encryptionSettings = new EncryptionSettings();
		// encryptionSettings.serverid = new BigInteger(130,
		// Main.random).toString(32).substring(0, 20);
		encryptionSettings.serverid = "";
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Server platfrom unsuported, cannot hash rsa", ex);
		}
		keyPairGenerator.initialize(1024);
		encryptionSettings.keyPair = keyPairGenerator.genKeyPair();
		encryptionSettings.verifyToken = new byte[16];
		Main.random.nextBytes(encryptionSettings.verifyToken);
	}

	public void forcePacketFlush() {
		synchronized (packets) {
			Packet p;
			while ((p = packets.poll()) != null) {
				ctx.write(p);
				//EnderLogger.debug("Out: "+p.toString());
				p.onSend(this);
			}
			ctx.flush();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (this.ctx == null)
			this.ctx = ctx;
		((Packet) msg).onRecieve(this);
		// EnderLogger.debug("in: " + msg);
		forcePacketFlush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		EnderLogger.exception(cause);
		ctx.close();
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.onDisconnect();
		super.channelInactive(ctx);
	}

	private void onDisconnect() {
		if (this.isConnected == false) return;
		synchronized (packets) {
			if (this.isConnected == false) return;
			this.isConnected = false;
			this.packets.clear();
			if (player != null && player.isOnline()) {
				final EnderPlayer subPlayer = player;
				Main.getInstance().sendToMainThread(new Runnable() {

					@Override
					public void run() {
						subPlayer.onDisconnect();
					}
				});
				player.isOnline = false;
			}
		}
	}

	private MinecraftServerCodex codex;

	public MinecraftServerCodex createCodex() {
		return codex = new MinecraftServerCodex(this);
	}

	public MinecraftServerCodex getCodex() {
		return codex;
	}

	public void sendPacket(Packet... packets) {
		if (this.isConnected == false) return;
		synchronized (packets) {
			if (this.isConnected == false) return;
			for (Packet packet : packets) {
				this.packets.offer(packet);
			}
		}
	}

	public void setupEncryption(final SecretKey key) throws IOException {
		final Cipher decrypter = generateKey(2, key);
		final Cipher encrypter = generateKey(1, key);
		ctx.pipeline().addBefore("packet_rw_converter", "decrypt", new MessageToMessageDecoder<ByteBuf>() {

			NetworkEncrypter chipper = new NetworkEncrypter(decrypter);

			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws ShortBufferException {
				System.out.println("Decrypting " + in.readableBytes());
				this.chipper.decrypt(ctx, in);
			}

		});
		ctx.pipeline().addBefore("packet_rw_converter", "encrypt", new MessageToByteEncoder<ByteBuf>() {

			NetworkEncrypter chipper = new NetworkEncrypter(encrypter);

			@Override
			protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
				System.out.println("Encrypting " + msg.readableBytes());
				this.chipper.encrypt(msg, out);
			}

		});
	}

	private static Cipher generateKey(int keyType, Key secretKey) throws IOException {
		try {
			Cipher localCipher = Cipher.getInstance("AES/CFB8/NoPadding");
			localCipher.init(keyType, secretKey, new IvParameterSpec(secretKey.getEncoded()));
			return localCipher;
		} catch (GeneralSecurityException localGeneralSecurityException) {
			throw new IOException("Unable to generate a encryption key", localGeneralSecurityException);
		}
	}

	public void spawnPlayer() {
		if (player != null)
			throw new IllegalStateException();
		if (this.uuid == null) {
			this.disconnect("Illegal uuid");
			return;
		}
		if (this.skinBlob == null)
			this.skinBlob = PlayerTextureStore.DEFAULT_STORE; // Null oject
																// design
																// pattern

		player = new EnderPlayer(wantedName, this, uuid, this.skinBlob);
		final Object lock = new Object();
		synchronized (lock) {
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					Main.getInstance().onlinePlayers.add(player);
					try {

						sendPacket(new PacketOutLoginSucces(player.uuid.toString(), player.getPlayerName()));
						sendPacket(new PacketOutJoinGame(player.getEntityId(), (byte) GameMode.SURVIVAL.getId(), (byte) 0, (byte) 1, (byte) 60, "default", false));
						sendPacket(new PacketOutUpdateTime(0,Main.getInstance().mainWorld.getTime()));
						EnderWorld mainWorld = Main.getInstance().mainWorld;
						Location spawn = mainWorld.getSpawn();
						Location loc = player.getLocation();
						loc.setX(spawn.getX());
						loc.setY(spawn.getY());
						loc.setZ(spawn.getZ());
						loc.setYaw(spawn.getYaw());
						loc.setPitch(spawn.getPitch());

						mainWorld.doChunkUpdatesForPlayer(player, player.chunkInformer, 1);
						player.onSpawn();
						sendPacket(new PacketOutSpawnPosition(spawn));

						int i = 0;
						if (player.isCreative)
							i = (byte) (i | 0x1);
						if (player.isFlying)
							i = (byte) (i | 0x2);
						if (player.canFly)
							i = (byte) (i | 0x4);
						if (player.godMode)
							i = (byte) (i | 0x8);

						sendPacket(new PacketOutPlayerAbilities((byte) i, 0.1F, 0.1F));
						sendPacket(new PacketOutPlayerPositionLook(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), (byte) 0b00000));
						sendPacket(new PacketOutUpdateHealth(player.getHealth(), player.food, player.foodSaturation));

					} catch (Exception e) {
						EnderLogger.exception(e);
					} finally {
						synchronized (lock) {
							lock.notifyAll();
						}
					}

				}
			});
			try {
				lock.wait();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

	}

	public class EncryptionSettings {

		private String serverid;
		private KeyPair keyPair;
		private byte[] verifyToken;

		public String getServerid() {
			return serverid;
		}

		public KeyPair getKeyPair() {
			return keyPair;
		}

		public byte[] getVerifyToken() {
			return verifyToken;
		}
	}

	/**
	 * Disconnected the player with the specified warning
	 * @param message the disconnect message
	 * @deprecated This method doesn't know the real cause of the disconnect, use disconnect(Message, boolean) instead
	 */
	@Deprecated
	public void disconnect(String message) {
		this.disconnect(message, true);
	}
	
	public void disconnect(String message, boolean byError) {
		this.disconnect(new SimpleMessage(message), byError);
	}
	
	@Deprecated
	public void disconnect(Message message) {
		this.disconnect(message, true);
	}

	/**
	 * Disconnects the player
	 * @param message the message to kick the player
	 * @param byError if true, then the reason of the kick is printed in the console
	 */
	public void disconnect(Message message, boolean byError) {
		try {
			this.ctx.channel().pipeline().addFirst("packet_r_disconnected", new DiscardingReader());
			Level level = byError ? Level.WARNING : Level.INFO;
			if (this.player == null)
				EnderLogger.logger.log(level, "Kicking unregistered channel " + this.digitalName() + ": " + message.toPlainText());
			else
				EnderLogger.logger.log(level, "Kicking " + this.digitalName() +": " + message.toPlainText());
			Packet p = codex.getDisconnectionPacket(message);
			EnderLogger.warn("Kicking ");
			this.ctx.channel().pipeline().addFirst(new DiscardingReader());
			if (p != null)
				ctx.write(p);
		} catch (Exception ex) {
			EnderLogger.exception(ex);
		}
		finally
		{
			this.onDisconnect();
			ctx.close();
		}
	}
	
	public void enableCompression(){
		this.ctx.pipeline().addBefore("packet_rw_converter", "packet_r_decompressor", new MinecraftDecompressionCodex());
		this.ctx.pipeline().addBefore("packet_rw_converter", "packet_w_compressor", new MinecraftCompressionCodex());
	}
	public String digitalName()
	{
		InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
		return "["+address.getAddress()+"|"+address.getPort()+"]-("+String.valueOf(this.wantedName)+"";
	}
}
