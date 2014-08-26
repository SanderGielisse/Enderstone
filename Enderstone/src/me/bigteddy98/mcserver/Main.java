package me.bigteddy98.mcserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.yaml.snakeyaml.Yaml;

import me.bigteddy98.mcserver.entity.EnderPlayer;
import me.bigteddy98.mcserver.packet.play.PacketKeepAlive;
import me.bigteddy98.mcserver.regions.EnderWorld;

public class Main implements Runnable {

	public static final String NAME = "Enderstone";
	public static final String PROTOCOL_VERSION = "1.7.6";
	public static final int PROTOCOL = 5;
	public static final String[] AUTHORS = new String[] { "bigteddy98", "ferrybig" };

	public static final Random random = new Random();
	private final int port;
	public final EnderWorld mainWorld = new EnderWorld();
	public volatile boolean isRunning = true;

	private static Main instance;

	public final List<EnderPlayer> onlinePlayers = new ArrayList<>();
	private final List<Runnable> sendToMainThread = Collections.synchronizedList(new ArrayList<Runnable>());

	public Main(int port) {
		this.port = port;
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
		new Thread(new Main(25565)).start();
	}

	@Override
	public void run() {
		EnderLogger.info("Starting " + NAME + " server version " + PROTOCOL_VERSION + " at port " + this.port);
		EnderLogger.info("Authors: " + Arrays.asList(AUTHORS).toString());

		EnderLogger.info("Starting Netty Server...");
		
		
		
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
							run.run();
						}
						sendToMainThread.clear();
					}

					serverTick();

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

	private int latestKeepAlive = 0;

	private void serverTick() {

		if (latestKeepAlive++ % (20 * 10) == 0) {
			for (EnderPlayer p : onlinePlayers) {
				p.getNetworkManager().sendPacket(new PacketKeepAlive(p.keepAliveID = random.nextInt(Integer.MAX_VALUE)));
			}
		}
	}
}
