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

import org.enderstone.server.api.event.Cancellable;
import org.enderstone.server.api.event.Event;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.commands.CommandMap;
import org.enderstone.server.commands.enderstone.CraftingDebugCommand;
import org.enderstone.server.commands.enderstone.DebugCommand;
import org.enderstone.server.commands.enderstone.LagCommand;
import org.enderstone.server.commands.enderstone.PingCommand;
import org.enderstone.server.commands.enderstone.QuitCommand;
import org.enderstone.server.commands.enderstone.VersionCommand;
import org.enderstone.server.commands.enderstone.WorldCommand;
import org.enderstone.server.commands.vanila.KillCommand;
import org.enderstone.server.commands.vanila.StopCommand;
import org.enderstone.server.commands.vanila.TeleportCommand;
import org.enderstone.server.commands.vanila.TellCommand;
import org.enderstone.server.entity.EnderEntity;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.inventory.DefaultCraftingRecipes;
import org.enderstone.server.packet.ConnectionInitializer;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketKeepAlive;
import org.enderstone.server.packet.play.PacketOutChatMessage;
import org.enderstone.server.packet.play.PacketOutUpdateTime;
import org.enderstone.server.regions.EnderWorld;
import org.enderstone.server.regions.generators.FlyingIslandsGenerator;
import org.enderstone.server.regions.generators.SimpleGenerator;
import org.enderstone.server.util.NettyThreadFactory;
import org.enderstone.server.uuid.UUIDFactory;

public class Main implements Runnable {

	public static final String NAME = "Enderstone";
	public static final String VERSION = "1.0.0";
	public static final String PROTOCOL_VERSION = "1.8";
	public static final int EXCEPTED_TICK_RATE = 20;
	public static final int EXCEPTED_SLEEP_TIME = 1000 / EXCEPTED_TICK_RATE;
	public static final int CANT_KEEP_UP_TIMEOUT = -10000;
	public static final int MAX_VIEW_DISTANCE = 10;
	public static final int MAX_NETTY_BOSS_THREADS = 4;
	public static final int MAX_NETTY_WORKER_THREADS = 8;
	public static final int MAX_SLEEP = 100;
	public static final int DEFAULT_PROTOCOL = 47;
	public static final Set<Integer> SUPPORTED_PROTOCOLS = Collections.unmodifiableSet(new HashSet<Integer>() {
		private static final long serialVersionUID = 1L;

		{
			this.add(47); // 1.8
		}
	});
	public static final String[] AUTHORS = new String[] { "Sander Gielisse [sander2798]", "Fernando van Loenhout [ferrybig]" };
	public static final String[] TOP_CONTRIBUTORS = new String[] { "Gyroninja" };
	public static final Random random = new Random();
	public volatile Thread mainThread;
	public final List<Thread> listenThreads = new CopyOnWriteArrayList<>();
	public boolean onlineMode = false;

	public Properties prop = null;
	public volatile String motd;
	public volatile int maxPlayers = 20;
	public UUIDFactory uuidFactory = new UUIDFactory();
	public String FAVICON = null;
	public int port;
	public boolean doPhysics = true;
	public volatile boolean isRunning = true;
	private long tick = 0;
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
		commands.registerCommand(new LagCommand());
	}

	private static Main instance;
	/**
	 * This array is used to store the last lag of the server, it can be used by plugins to calculate the lag
	 */
	private final long[] lastTickSlices = new long[128];
	private int lastTickPointer = 0;

	public volatile int playerCount = 0; // high performance solution for getting the onlinePlayers.size() from an async thread
	public final Set<EnderPlayer> onlinePlayers = new HashSet<>();
	public final List<EnderWorld> worlds = new ArrayList<>();
	private final List<Runnable> sendToMainThread = new ArrayList<Runnable>(); // don't forget to synchronize

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
		EnderLogger.info("Top contributors: " + Arrays.asList(TOP_CONTRIBUTORS).toString() + " <--- Thanks to them!");
		EnderLogger.info("Loading server.properties file...");
		this.loadConfigFromDisk();
		this.motd = (String) prop.get("motd");
		// TODO read max players from config
		// this.maxPlayers = ;
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

		EnderLogger.info("Server ready... Starting required threads now!");

		final ThreadGroup nettyListeners = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Netty Listeners");
		for (final int nettyPort : new int[] { this.port }) {

			Thread t;
			(t = new Thread(nettyListeners, new Runnable() {

				@Override
				public void run() {
					EnderLogger.info("Started Netty Server at port " + nettyPort + "...");
					ThreadGroup group = new ThreadGroup(nettyListeners, "Listener-" + nettyPort);
					EventLoopGroup bossGroup = new NioEventLoopGroup(MAX_NETTY_BOSS_THREADS, new NettyThreadFactory(group, "boss"));
					EventLoopGroup workerGroup = new NioEventLoopGroup(MAX_NETTY_WORKER_THREADS, new NettyThreadFactory(group, "worker"));

					try {
						ServerBootstrap bootstrap = new ServerBootstrap();
						bootstrap.group(bossGroup, workerGroup);
						bootstrap.channel(NioServerSocketChannel.class);
						bootstrap.childHandler(new ConnectionInitializer());

						bootstrap.bind(nettyPort).sync().channel().closeFuture().sync();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						EnderLogger.info("Stopped Netty Server at port " + nettyPort + "...");
						scheduleShutdown();
						bossGroup.shutdownGracefully();
						workerGroup.shutdownGracefully();
					}
				}
			}, "Listener-" + nettyPort)).start();
			this.listenThreads.add(t);
		}

		(mainThread = new Thread(new Runnable() {
			long lastTick = System.currentTimeMillis();

			@Override
			public void run() {
				EnderLogger.info("Main Server Thread initialized and started!");
				EnderLogger.info("" + NAME + " Server started, " + PROTOCOL_VERSION + " clients can now connect to port " + port + "!");

				// TODO support multiple worlds with a simple and good working system
				worlds.add(new EnderWorld("world1", new SimpleGenerator()));
				worlds.add(new EnderWorld("world2", new FlyingIslandsGenerator()));

				try {
					while (Main.this.isRunning) {
						mainServerTick();
					}
				} catch (InterruptedException e) {
					Main.this.isRunning = false;
					Thread.currentThread().interrupt();
				} catch (RuntimeException ex) {
					EnderLogger.error("CRASH REPORT! (this should not happen!)");
					EnderLogger.error("Main thread has shut down, this shouldn't happen!");
					EnderLogger.exception(ex);
					EnderLogger.error("Server was processing tick " + tick);
					EnderLogger.error("Last succesfull tick was " + new Date(lastTick).toString());
				} finally {
					Main.this.isRunning = false;
					Main.getInstance().directShutdown();
					EnderLogger.info("Main Server Thread stopped!");
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
				Main.this.lastTickSlices[Main.this.lastTickPointer] = sleepTime;
				if (++Main.this.lastTickPointer >= Main.this.lastTickSlices.length)
					Main.this.lastTickPointer = 0;
				if (sleepTime < Main.CANT_KEEP_UP_TIMEOUT) {
					this.warn("Can't keep up! " + -(sleepTime / Main.EXCEPTED_SLEEP_TIME) + " ticks behind!");
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
		}, "ServerThread")).start();

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
		} catch (Exception e) {
			return false;
		}
	}

	public long getCurrentServerTick() {
		return this.tick;
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

	public EnderPlayer getPlayer(UUID uuid) {
		for (EnderPlayer ep : this.onlinePlayers) {
			if (ep.uuid.equals(uuid)) {
				return ep;
			}
		}
		return null;
	}

	private long latestKeepAlive = 0;
	private long latestChunkUpdate = 0;

	private void serverTick(long tick) {
		int recepies = DefaultCraftingRecipes.serverTick();
		if (recepies != -1) {
			EnderLogger.info(recepies + " crafting recipes listeners loaded!");
		}
		this.playerCount = this.onlinePlayers.size();
		boolean doKeepAliveUpdate = (latestKeepAlive++ & 0b0011_1111) == 0; // faster than % 64 == 0
		boolean doChunkUpdate = (latestChunkUpdate++ & 0b0001_1111) == 0; // faster than % 31 == 0
		boolean doUpdateTimeAndWeather = (tick & 0b0011_1111) == 0; // faster than % 64 == 0
		for (EnderPlayer p : onlinePlayers) {
			p.serverTick();
			if (doKeepAliveUpdate) {
				p.getNetworkManager().sendPacket(new PacketKeepAlive(p.keepAliveID = random.nextInt(Integer.MAX_VALUE)));
			}
			if (doChunkUpdate && !p.isDead()) {
				p.getWorld().doChunkUpdatesForPlayer(p, p.chunkInformer, Math.min(p.clientSettings.renderDistance - 1, MAX_VIEW_DISTANCE));
				p.updatePlayers(onlinePlayers);
			}
			for (EnderWorld world : worlds) {
				world.updateEntities(onlinePlayers);
			}
			if (doUpdateTimeAndWeather) {
				p.getNetworkManager().sendPacket(new PacketOutUpdateTime(tick, this.getWorld(p).getTime()));
			}
		}
		for (EnderWorld world : worlds) {
			world.serverTick();
		}
	}

	public void broadcastMessage(Message message) {
		EnderLogger.info(message.toPlainText());
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

	public EnderWorld getWorld(EnderPlayer player) {
		for (EnderWorld world : this.worlds) {
			if (world.players.contains(player)) {
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

	public boolean callEvent(Event e) {
		// TODO call events
		if (e instanceof Cancellable) {
			((Cancellable) e).isCancelled();
		}
		return false;
	}

	public long[] getLastLag() {
		long[] last = new long[this.lastTickSlices.length];
		if (this.lastTickPointer == 0)
			System.arraycopy(this.lastTickSlices, 0, last, 0, last.length);
		else {
			System.arraycopy(this.lastTickSlices, this.lastTickPointer, last, 0, last.length - this.lastTickPointer);
			System.arraycopy(this.lastTickSlices, 0, last, last.length - this.lastTickPointer, this.lastTickPointer);
		}
		return last;
	}
}
