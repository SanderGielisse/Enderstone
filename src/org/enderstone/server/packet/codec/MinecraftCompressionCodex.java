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
package org.enderstone.server.packet.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;
import org.enderstone.server.packet.Packet;

public class MinecraftCompressionCodex extends MessageToByteEncoder<ByteBuf> {

	private final Deflater compressor = new Deflater(8);

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf incoming, ByteBuf outgoing) throws Exception {
		int startSize = Packet.readVarInt(incoming);		
		
		{ // compress it
			ByteBuf temporarilyBuf = Unpooled.buffer();
			
			byte[] array = new byte[startSize];
			incoming.readBytes(array); //the whole uncompressed packet
			//Packet.writeVarInt(array.length, temporarilyBuf);

			compressor.setInput(array);
			compressor.finish();

			byte[] buffer = new byte[1 * 1024];
			do {
				int size = compressor.deflate(buffer);
				temporarilyBuf.writeBytes(buffer, 0, size);
			} while (!compressor.finished());
			compressor.reset();
			
			Packet.writeVarInt(temporarilyBuf.readableBytes() + Packet.getVarIntSize(startSize), outgoing);
			Packet.writeVarInt(startSize, outgoing);
			outgoing.writeBytes(temporarilyBuf);
			temporarilyBuf.release();
		}
	}
}
