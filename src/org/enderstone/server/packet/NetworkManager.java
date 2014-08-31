package org.enderstone.server.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.GameMode;
import org.enderstone.server.entity.PlayerTextureStore;
import org.enderstone.server.packet.codec.MinecraftServerCodex;
import org.enderstone.server.packet.login.PacketOutLoginSucces;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutJoinGame;
import org.enderstone.server.packet.play.PacketOutPlayerAbilities;
import org.enderstone.server.packet.play.PacketOutPlayerPositionLook;
import org.enderstone.server.packet.play.PacketOutSpawnPosition;
import org.enderstone.server.packet.play.PacketOutUpdateHealth;

public class NetworkManager extends ChannelHandlerAdapter {

	public ChannelHandlerContext ctx;
	private Channel channel;
	public EnderPlayer player;
	public String wantedName;
	private EncryptionSettings encryptionSettings;
	public UUID uuid;
	public PlayerTextureStore skinBlob;

	public PacketHandshake latestHandshakePacket;
	public volatile int handShakeStatus = -1;
	private int length;

	private final Queue<Packet> packets = new LinkedList<>();

	public EncryptionSettings getEncryptionSettings() {
		return encryptionSettings;
	}

	public void regenerateEncryptionSettings() {
		encryptionSettings = new EncryptionSettings();
		//encryptionSettings.serverid = new BigInteger(130, Main.random).toString(32).substring(0, 20);
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

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (this.ctx == null) this.ctx = ctx;
		((Packet) msg).onRecieve(this);
		//EnderLogger.debug("in: " + msg);
		
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
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		EnderLogger.exception(cause);
		this.channelInactive(ctx);
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (player != null && player.isOnline()) {
			final EnderPlayer subPlayer = player;
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					try {
						subPlayer.onDisconnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (Main.getInstance().onlinePlayers.contains(subPlayer)) {
						Main.getInstance().onlinePlayers.remove(subPlayer);

						for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
							for (String name : ep.visiblePlayers) {
								if (name.equals(subPlayer.getPlayerName()) && !subPlayer.getPlayerName().equals(ep.getPlayerName())) {
									ep.getNetworkManager().sendPacket(new PacketOutEntityDestroy(new Integer[]{subPlayer.getEntityId()}));
								}
							}
						}
					}
				}
			});
			player.isOnline = false;
		}
		super.channelInactive(ctx);
	}
	private MinecraftServerCodex codex;

	public MinecraftServerCodex createCodex() {
		return codex = new MinecraftServerCodex(this);
	}

	public MinecraftServerCodex getCodex() {
		return codex;
	}

	public synchronized void sendPacket(Packet... packets) {
		for (Packet packet : packets) {
			this.packets.offer(packet);
		}
		ctx.flush();
	}

	public void setupEncryption(final SecretKey key) throws IOException {
		final Cipher decrypter = generateKey(2, key);
		final Cipher encrypter = generateKey(1, key);
		ctx.pipeline().addFirst("decrypt", new MessageToMessageDecoder<ByteBuf>() {

			NetworkEncrypter chipper = new NetworkEncrypter(decrypter);

			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws ShortBufferException {
				System.out.println("Decrypting " + in.readableBytes());
				this.chipper.decrypt(ctx, in);
			}

		});
		ctx.pipeline().addFirst("encrypt", new MessageToByteEncoder<ByteBuf>() {

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
		if (player != null) throw new IllegalStateException();
		if (this.uuid == null) {
			this.disconnect("Illegal uuid");
			return;
		}
		if (this.skinBlob == null)
			this.skinBlob = PlayerTextureStore.DEFAULT_STORE; // Null oject design pattern

		player = new EnderPlayer(wantedName, this, uuid, this.skinBlob);
		final Object lock = new Object();
		synchronized (lock) {
			Main.getInstance().sendToMainThread(new Runnable() {

				@Override
				public void run() {
					Main.getInstance().onlinePlayers.add(player);
					try {

						sendPacket(new PacketOutLoginSucces(player.uuid.toString(), player.getPlayerName()));

						sendPacket(new PacketOutJoinGame(player.getEntityId(), (byte) GameMode.SURVIVAL.getId(), (byte) 0, (byte) 1, (byte) 60, "default"));

						player.getLocation().setX(0);
						player.getLocation().setY(100);
						player.getLocation().setZ(0);

						Main.getInstance().mainWorld.doChunkUpdatesForPlayer(player, player.chunkInformer, 1);
						player.onSpawn();
						sendPacket(new PacketOutSpawnPosition(0, 100, 0));

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
						sendPacket(new PacketOutPlayerPositionLook(0, 100, 0, 0F, 0F, false));
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

	public void disconnect(String message) {
		try {
			EnderLogger.error("Disconnecting unverified connection (name: " + this.wantedName + "): " + message);
			ctx.close();
		} catch (Exception ex) {
			EnderLogger.exception(ex);
		}
	}
}
