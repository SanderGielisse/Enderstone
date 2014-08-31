package org.enderstone.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.enderstone.server.packet.NetworkManager;

public class MinecraftServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline line = channel.pipeline();
		NetworkManager manager = new NetworkManager();
		line.addLast("packet_r_timeout",new ReadTimeoutHandler(30, TimeUnit.SECONDS));
		line.addLast("packet_rw_converter", manager.createCodex());
		line.addLast("packet_rw_reader", manager);
	}
}
