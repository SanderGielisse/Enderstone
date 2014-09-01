
package org.enderstone.server.packet.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.enderstone.server.EnderLogger;

/**
 *
 * @author Fernando
 */
public class DiscardingReader extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ((ByteBuf) msg).release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        EnderLogger.exception(cause);
        ctx.close();
    }
}
