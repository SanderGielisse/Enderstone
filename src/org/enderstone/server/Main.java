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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.enderstone.CraftingDebugCommand;
import org.enderstone.server.commands.enderstone.DebugCommand;
import org.enderstone.server.commands.enderstone.PingCommand;
import org.enderstone.server.commands.enderstone.QuitCommand;
import org.enderstone.server.commands.enderstone.VersionCommand;
import org.enderstone.server.commands.enderstone.WorldCommand;
import org.enderstone.server.commands.vanila.KillCommand;
import org.enderstone.server.commands.vanila.StopCommand;
import org.enderstone.server.commands.vanila.TeleportCommand;
import org.enderstone.server.commands.vanila.TellCommand;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketKeepAlive;
import org.enderstone.server.packet.play.PacketOutChatMessage;
import org.enderstone.server.packet.play.PacketOutUpdateTime;
import org.enderstone.server.permissions.Operator;
import org.enderstone.server.permissions.OperatorLoader;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.generators.FlatLandGenerator;
import org.enderstone.server.regions.generators.FlyingIslandsGenerator;
import org.enderstone.server.regions.generators.TimTest;
import org.enderstone.server.uuid.UUIDFactory;

public class Main implements Runnable {

	public static final String NAME = "Enderstone";
	public static final String VERSION = "1.0.0";
	public static final String PROTOCOL_VERSION = "1.8";
	public static final int EXCEPTED_SLEEP_TIME = 1000 / 20;
	public static final int CANT_KEEP_UP_TIMEOUT = -10000;
	public static final int MAX_VIEW_DISTANCE = 10;
	public static final int MAX_SLEEP = 100;
	public static final int DEFAULT_PROTOCOL = 47;
	public static final Set<Integer> PROTOCOL = Collections.unmodifiableSet(new HashSet<Integer>() {
		private static final long serialVersionUID = 1L;

		{
			this.add(47); // 1.8
		}
	});
	public static final String[] AUTHORS = new String[]{"bigteddy98", "ferrybig", "timbayens"};
	public static final Random random = new Random();
	public volatile Thread mainThread;
	public final List<Thread> listenThreads = new CopyOnWriteArrayList<>();
	public boolean onlineMode = false;
	public List<Operator> operators = new OperatorLoader().load();

	public Properties prop = null;
	public UUIDFactory uuidFactory = new UUIDFactory();
	public String FAVICON = null;
	public int port;
	public volatile boolean isRunning = true;
	public final CommandMap commands;

	{
		commands = new CommandMap();
		commands.registerCommand(new TellCommand());
		commands.registerCommand(new PingCommand());
		commands.registerCommand(new VersionCommand());
		commands.registerCommand(new TeleportCommand());
		commands.registerCommand(new StopCommand());
		commands.registerCommand(new KillCommand());
		commands.registerCommand(new QuitCommand());
		commands.registerCommand(new DebugCommand());
		commands.registerCommand(new WorldCommand());
		commands.registerCommand(new CraftingDebugCommand());
	}

	private static Main instance;

	public final Set<EnderPlayer> onlinePlayers = new HashSet<>();
	public final List<EnderWorld> worlds = new ArrayList<>();
	private final List<Runnable> sendToMainThread = Collections.synchronizedList(new ArrayList<Runnable>());

	public static Main getInstance() {
		return instance;
	}

	public void sendToMainThread(Runnable run) {
		if (isCurrentThreadMainThread())
			run.run();
		else
			synchronized (sendToMainThread) {
				sendToMainThread.add(run);
			}
	}

	public static void main(String[] args) {
		new Main().run();
	}

	@Override
	public void run() {
		Main.instance = this;
		EnderLogger.info("Starting " + NAME + " " + VERSION + " server version " + PROTOCOL_VERSION + ".");
		EnderLogger.info("Authors: " + Arrays.asList(AUTHORS).toString());
		EnderLogger.info("Loading server.properties file...");
		this.loadConfigFromDisk();
		EnderLogger.info("Loaded server.properties file!");

		EnderLogger.info("Loading favicon...");
		try {
			if (readFavicon())
				EnderLogger.info("Loaded server-icon.png!");
		} catch (FileNotFoundException e) {
			EnderLogger.info("server-icon.png not found!");
		} catch (IOException e) {
			EnderLogger.warn("Error while reading server-icon.png!");
			EnderLogger.exception(e);
		}
		
		ThreadGroup nettyListeners = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Netty Listeners");
		EnderLogger.info("Starting Netty listeners... [" + this.port + "]");
		for (final int nettyPort : new int[]{this.port}) {

			Thread t;
			(t = new Thread(nettyListeners, new Runnable() {

				@Override
				public void run() {
					EnderLogger.info("[Netty] Started Netty Server at port " + nettyPort + "...");
					EventLoopGroup bossGroup = new NioEventLoopGroup();
					EventLoopGroup workerGroup = new NioEventLoopGroup();

					try {
						ServerBootstrap bootstrap = new ServerBootstrap();
						bootstrap.group(bossGroup, workerGroup);
						bootstrap.channel(NioServerSocketChannel.class);
						bootstrap.childHandler(new MinecraftServerInitializer());

						bootstrap.bind(nettyPort).sync().channel().closeFuture().sync();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						scheduleShutdown();
						bossGroup.shutdownGracefully();
						workerGroup.shutdownGracefully();
					}
					EnderLogger.info("[Netty] Stopped Netty Server at port " + nettyPort + "...");
				}
			}, "Netty listener-" + nettyPort)).start();
			this.listenThreads.add(t);
		}

		EnderLogger.info("Initializing main Server Thread...");
		(mainThread = new Thread(new Runnable() {
			long lastTick = System.currentTimeMillis();
			long tick = 0;

			@Override
			public void run() {
				EnderLogger.info("[ServerThread] Main Server Thread initialized and started!");
				EnderLogger.info("[ServerThread] " + NAME + " Server started, " + PROTOCOL_VERSION + " clients can now connect to port " + port + "!");

				worlds.add(new EnderWorld("world1", new FlyingIslandsGenerator()));
				worlds.add(new EnderWorld("world2", new TimTest()));
				
				try {
					while (Main.this.isRunning) {
						mainServerTick();
					}
				} catch (InterruptedException e) {
					Main.this.isRunning = false;
					Thread.currentThread().interrupt();
				} catch (RuntimeException ex) {
					EnderLogger.error("[ServerThread] CRASH REPORT! (this should not happen!)");
					EnderLogger.error("[ServerThread] Main thread has shutdown, this shouldn't happen!");
					EnderLogger.exception(ex);
					EnderLogger.error("[ServerThread] Server is inside tick " + tick);
					EnderLogger.error("[ServerThread] Last tick was in " + new Date(lastTick).toString());
				} finally {
					Main.this.isRunning = false;
					Main.getInstance().directShutdown();
					EnderLogger.info("[ServerThread] Main Server Thread stopped!");
				}
			}

			private void mainServerTick() throws InterruptedException {
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				synchronized (sendToMainThread) {
					for (Runnable run : sendToMainThread) {
						try {
							run.run();
						} catch (Exception e) {
							EnderLogger.warn("Problem while executing task " + run.toString());
							EnderLogger.exception(e);
						}
					}
					sendToMainThread.clear();
				}

				try {
					serverTick(tick);
				} catch (Exception e) {
					EnderLogger.error("Problem while running ServerTick()");
					EnderLogger.exception(e);
				}
				this.lastTick += Main.EXCEPTED_SLEEP_TIME;
				long sleepTime = (lastTick) - System.currentTimeMillis();
				if (sleepTime < Main.CANT_KEEP_UP_TIMEOUT) {
					this.warn("Can't keep up! " + (sleepTime / Main.EXCEPTED_SLEEP_TIME) + " ticks behind!");
					this.lastTick = System.currentTimeMillis();
				} else if (sleepTime > Main.MAX_SLEEP) {
					this.warn("Did the system time change?");
					this.lastTick = System.currentTimeMillis();
				} else if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
				tick++;
			}

			public void warn(String warn) {
				EnderLogger.warn("[ServerThread] [tick-" + tick + "] " + warn);
			}
		}, "Enderstone server thread.")).start();

		ThreadGroup shutdownHooks = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Shutdown hooks");
		Runtime.getRuntime().addShutdownHook(new Thread(shutdownHooks, new Runnable() {

			@Override
			public void run() {
				Main.this.scheduleShutdown();
				boolean interrupted = false;
				boolean joined = false;
				do {
					try {
						mainThread.join();
						joined = true;
					} catch (InterruptedException ex) {
						interrupted = true;
					}
				} while (!joined);
				if (interrupted)
					Thread.currentThread().interrupt();
			}
		}, "Server stopping"));
	}

	private boolean readFavicon() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			BufferedImage image = ImageIO.read(new File("server-icon.png"));
			if (image.getWidth() == 64 && image.getHeight() == 64) {
				ImageIO.write(image, "png", baos);
				baos.flush();
				FAVICON = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
				return true;
			} else {
				EnderLogger.warn("Your server-icon.png needs to be 64*64!");
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}

	public void saveConfigToDisk(boolean defaultt) {
		try (OutputStream output = new FileOutputStream("server.properties")) {
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
		try (InputStream input = new FileInputStream("server.properties")) {
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

	private void serverTick(long tick) {
		for (EnderPlayer ep : onlinePlayers) {
			ep.serverTick();
		}

		if ((latestKeepAlive++ & 0b0011_1111) == 0) { // faster than % 64 == 0
			for (EnderPlayer p : onlinePlayers) {
				p.getNetworkManager().sendPacket(new PacketKeepAlive(p.keepAliveID = random.nextInt(Integer.MAX_VALUE)));
			}
		}

		if ((latestChunkUpdate++ & 0b0001_1111) == 0) { // faster than % 31 == 0
			for (EnderPlayer p : onlinePlayers) {
				if (p.isDead()) {
					p.networkManager.forcePacketFlush();
					continue;
				}
				p.getWorld().doChunkUpdatesForPlayer(p, p.chunkInformer, Math.min(p.clientSettings.renderDistance - 1, MAX_VIEW_DISTANCE));
				p.updatePlayers(onlinePlayers);
			}
			for (EnderWorld world : worlds) {
				world.updateEntities(onlinePlayers);
			}
		}

		if ((tick & 0b0011_1111) == 0){ // faster than % 64 == 0
			for (EnderPlayer p : onlinePlayers) {
				p.getNetworkManager().sendPacket(new PacketOutUpdateTime(tick, this.getWorld(p).getTime()));
			}
		}
		for(EnderWorld world : worlds){
			world.serverTick();
		}
	}

	public void broadcastMessage(Message message) {
		Packet p = new PacketOutChatMessage(message, (byte) 1);
		for (EnderPlayer player : Main.getInstance().onlinePlayers) {
			player.getNetworkManager().sendPacket(p);
		}
	}

	/**
	 * Schedule a server shutdown, calling this methodes says to the main thread that the server need to shutdown
	 */
	public void scheduleShutdown() {
		this.mainThread.interrupt();
		isRunning = false;
	}

	/**
	 * Any mainthread-shutdown logic belongs to this method
	 */
	private void directShutdown() {
		if(this.operators.isEmpty()){
			this.operators.add(new Operator("sander2798", UUID.fromString("6743a814-9d41-4d33-af9e-e143bc2d462c")));
			this.operators.add(new Operator("ferrybig", UUID.fromString("a3cf4b48-220f-4604-83dd-314bab52b022")));
		}
		new OperatorLoader().write(operators);
		
		if (this.mainThread != null) {
			this.mainThread.interrupt();
		}
		for (Thread t : this.listenThreads) {
			t.interrupt();
		}
		boolean interrupted = false;
		for (Thread t : this.listenThreads) {
			boolean joined = false;
			do {
				try {
					t.join();
					joined = true;
				} catch (InterruptedException ex) {
					interrupted = true;
				}
			} while (!joined);
		}
		if (interrupted)
			Thread.currentThread().interrupt();
	}

	public EnderPlayer getPlayer(int entityId) {
		for (EnderPlayer ep : this.onlinePlayers) {
			if (ep.getEntityId() == entityId) {
				return ep;
			}
		}
		return null;
	}

	public static boolean isCurrentThreadMainThread() {
		return Main.getInstance().mainThread == Thread.currentThread();
	}
	
	public EnderWorld getWorld(EnderPlayer player){
		for(EnderWorld world : this.worlds){
			if(world.players.contains(player)){
				return world;
			}
		}
		return null;
	}

	public EnderEntity getEntityById(int targetId) {
		for (EnderWorld w : this.worlds) {
			for (EnderEntity e : w.entities) {
				if (e.getEntityId() == targetId) {
					return e;
				}
			}
		}
		for (EnderPlayer ep : this.onlinePlayers) {
			if (ep.getEntityId() == targetId) {
				return ep;
			}
		}
		return null;
	}
}
