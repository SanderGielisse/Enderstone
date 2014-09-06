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
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import java.util.zip.Inflater;
import org.enderstone.server.packet.Packet;

public class MinecraftDecompressionCodex extends ReplayingDecoder<Void>{

	private final Inflater decompressor = new Inflater();
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf compressedBuffer, List<Object> out) throws Exception {
		if(compressedBuffer.readableBytes() == 0){
			return;
		}
		int totalSize = Packet.readVarInt(compressedBuffer); //total byte count
		int uncompressedSize = Packet.readVarInt(compressedBuffer); //uncompressed size
		int dataSize = totalSize - Packet.getVarIntSize(uncompressedSize);
		
		ByteBuf uncompressedBuffer = Unpooled.buffer();
		
		if(uncompressedSize == 0){			
			Packet.writeVarInt(totalSize - Packet.getVarIntSize(0), uncompressedBuffer);
			uncompressedBuffer.writeBytes(compressedBuffer, 0, dataSize);
		}else{
			byte[] compressedPacket = new byte[totalSize - Packet.getVarIntSize(uncompressedSize)];
			compressedBuffer.readBytes(compressedPacket);
			
			decompressor.setInput(compressedPacket);
			byte[] uncompressedPacket = new byte[uncompressedSize];
			decompressor.inflate(uncompressedPacket);
			Packet.writeVarInt(uncompressedSize, uncompressedBuffer);
			uncompressedBuffer.writeBytes(uncompressedPacket);
			decompressor.reset();
		}
		out.add(uncompressedBuffer);
	}
}
