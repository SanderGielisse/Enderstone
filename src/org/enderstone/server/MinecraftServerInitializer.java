package org.enderstone.server;

import org.enderstone.server.packet.NetworkManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class MinecraftServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline line = channel.pipeline();
		line.addLast("packet_handler", new NetworkManager());
	}
}
