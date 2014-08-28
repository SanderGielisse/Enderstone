package org.enderstone.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;
import javax.imageio.ImageIO;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.enderstone.PingCommand;
import org.enderstone.server.commands.enderstone.VersionCommand;
import org.enderstone.server.commands.vanila.TellCommand;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.play.PacketKeepAlive;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.uuid.UUIDFactory;

public class Main implements Runnable {

	public static final String NAME = "Enderstone";
	public static final String PROTOCOL_VERSION = "1.7.6";
	public static final Set<Integer> PROTOCOL = Collections.unmodifiableSet(new HashSet<Integer>() {
		private static final long serialVersionUID = 1L;

		{
			this.add(5); // 1.7.9
		}
	});
	public static final String[] AUTHORS = new String[] { "bigteddy98", "ferrybig", "timbayens" };
	public static final Random random = new Random();

	public Properties prop = null;
	public UUIDFactory uuidFactory = new UUIDFactory();
	public String FAVICON = null;
	public int port;
	public final EnderWorld mainWorld = new EnderWorld();
	public volatile boolean isRunning = true;
	public final CommandMap commands;
	{
		commands = new CommandMap();
		commands.registerCommand(new TellCommand());
		commands.registerCommand(new PingCommand());
		commands.registerCommand(new VersionCommand());
	}
			
	private static Main instance;

	public final List<EnderPlayer> onlinePlayers = new ArrayList<>();
	private final List<Runnable> sendToMainThread = Collections.synchronizedList(new ArrayList<Runnable>());

	public Main() {
		instance = this;
	}

	public static Main getInstance() {
		return instance;
	}

	public void sendToMainThread(Runnable run) {
		synchronized (sendToMainThread) {
			sendToMainThread.add(run);
		}
	}

	public static void main(String[] args) {
		new Thread(new Main()).start();
	}

	@Override
	public void run() {
		EnderLogger.info("Starting " + NAME + " server version " + PROTOCOL_VERSION + ".");
		EnderLogger.info("Authors: " + Arrays.asList(AUTHORS).toString());

		EnderLogger.info("Loading config.ender file...");
		this.loadConfigFromDisk();

		EnderLogger.info("Loading favicon...");
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			BufferedImage image = ImageIO.read(new File("server-icon.png"));
			if (image.getWidth() == 64 && image.getHeight() == 64) {
				ImageIO.write(image, "png", baos);
				baos.flush();
				FAVICON = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
			} else {
				EnderLogger.exception(new IllegalArgumentException("Your server-icon.png needs to be 64*64!"));
			}
		} catch (IOException e) {
			EnderLogger.exception(new FileNotFoundException("server-icon.png not found!"));
		}

		EnderLogger.info("Favicon server-icon.png loaded!");

		EnderLogger.info("Starting Netty Server at port " + this.port + "...");

		new Thread(new Runnable() {

			@Override
			public void run() {
				EnderLogger.info("Netty Server Started!");

				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();

				try {
					ServerBootstrap bootstrap = new ServerBootstrap();
					bootstrap.group(bossGroup, workerGroup);
					bootstrap.channel(NioServerSocketChannel.class);
					bootstrap.childHandler(new MinecraftServerInitializer());

					bootstrap.bind(port).sync().channel().closeFuture().sync();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
				}
			}
		}).start();

		EnderLogger.info("Initializing main Server Thread...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRunning) {
					synchronized (sendToMainThread) {
						for (Runnable run : sendToMainThread) {
							try {
								run.run();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						sendToMainThread.clear();
					}

					try {
						serverTick();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					try {
						Thread.sleep(50L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		EnderLogger.info("Main Server Thread initialized and started!");
		EnderLogger.info(NAME + " Server started, " + PROTOCOL_VERSION + " clients can now connect to port " + this.port + "!");
	}

	public void saveConfigToDisk(boolean defaultt) {
		try (OutputStream output = new FileOutputStream("config.ender")) {
			if (defaultt) {
				prop.setProperty("motd", "Another Enderstone server!");
				prop.setProperty("port", "25565");
				prop.setProperty("max-players", "20");
				prop.setProperty("view-distance", "7");
			}
			prop.store(output, "Enderstone Server Config!");
		} catch (IOException e1) {
			EnderLogger.exception(e1);
		}
	}

	public Properties loadConfigFromDisk() {
		prop = new Properties();
		try (InputStream input = new FileInputStream("config.ender")) {
			prop.load(input);
			port = Integer.parseInt(prop.getProperty("port"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.saveConfigToDisk(true);
			this.loadConfigFromDisk();
		} catch (IOException e) {
			EnderLogger.exception(e);
		}
		return prop;
	}

	public EnderPlayer getPlayer(String name) {
		for (EnderPlayer ep : this.onlinePlayers) {
			if (ep.getPlayerName().equals(name)) {
				return ep;
			}
		}
		return null;
	}

	private int latestKeepAlive = 0;
	private int latestChunkUpdate = 0;

	private void serverTick() {
		if ((latestKeepAlive++ & 0b1111111) == 0) { // faster than % .. == 0
			for (EnderPlayer p : onlinePlayers) {
				p.getNetworkManager().sendPacket(new PacketKeepAlive(p.keepAliveID = random.nextInt(Integer.MAX_VALUE)));
			}
		}

		if ((latestChunkUpdate++ & 0b111111) == 0) { // faster than % .. == 0
			for (EnderPlayer p : onlinePlayers) {
				mainWorld.doChunkUpdatesForPlayer(p, p.chunkInformer, 10);
				p.updatePlayers(onlinePlayers);
			}
		}
	}
}
