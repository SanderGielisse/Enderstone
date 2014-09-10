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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.io.IOException;
import java.util.List;
import org.enderstone.server.api.messages.Message;
import org.enderstone.server.packet.HandshakeState;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketDataWrapper;
import org.enderstone.server.packet.PacketHandshake;
import org.enderstone.server.packet.PacketManager;
import org.enderstone.server.packet.login.PacketOutLoginPlayerDisconnect;
import org.enderstone.server.packet.play.PacketOutPlayerDisconnect;

/**
 *
 * @author Fernando
 */
public class MinecraftServerCodex extends ByteToMessageCodec<Packet> {

	HandshakeState state = HandshakeState.NEW;
	NetworkManager manager;

	public MinecraftServerCodex(NetworkManager manager) {
		super(Packet.class);
		this.manager = manager;
	}

	@Override
	public boolean acceptOutboundMessage(Object msg) throws Exception {
		return msg instanceof Packet;
	}
	
	

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buf) throws IOException {
		packet.writeFully(new PacketDataWrapper(manager, buf));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws DecodeException {
		PacketDataWrapper buf = new PacketDataWrapper(manager, buffer);
		while (buf.readableBytes() > 0) {
			buf.markReaderIndex();
			int readBytes = buf.readableBytes();
			int length = 0;
			{
				int bytes = 0;
				byte in;
				while (true) {
					if (readBytes < 1) {
						buf.resetReaderIndex();
						return;
					}
					in = buf.readByte();

					length |= (in & 0x7F) << (bytes++ * 7);

					if (bytes > 5) {
						throw new DecodeException("VarInt too big");
					}

					if ((in & 0x80) != 0x80) {
						break;
					}
				}
			}
			if (buf.readableBytes() < length) {
				buf.resetReaderIndex();
				return;
			}
			int id = buf.readVarInt();
			Packet in;
			try {
				switch (state) {
					case NEW: {
						PacketHandshake packet = (PacketHandshake) PacketManager.handshake.newInstance();
						packet.read(buf);
						switch (packet.getNextState()) {
							case 1:
								state = HandshakeState.STATUS;
								break;
							case 2:
								state = HandshakeState.LOGIN;
								break;
							case 3:
								state = HandshakeState.PLAY;
								break;
							default:
								throw new IOException("Invalid packet");
						}
						in = packet;
						
					}
					break;
					default: {
						Packet packet = PacketManager.getPacketInstance(id, state);
						if (packet == null)
							throw new DecodeException("Packet decode failed:"
									+ "\nPacket ID: 0x" + Integer.toHexString(id) + ""
									+ "\nHandshake state: " + this.state
									+ "\nPacket size: " + length
									+ "\nReason: Unknown packet");
						packet.read(buf);
						in = packet;
					}
					break;
				}
				//EnderLogger.warn("<--- Size: " + (in.getSize() + Packet.getVarIntSize(in.getSize())) + " Packet: " + in.toString());
				out.add(in);
			} catch (IOException | InstantiationException | IllegalAccessException ex) {
				throw new DecodeException("Packet decode failed:"
						+ "\nPacket ID: 0x" + Integer.toHexString(id) + ""
						+ "\nHandshake state: " + this.state
						+ "\nPacket size: " + length
						+ "\nReason: " + ex.getMessage(), ex);
			}
			buf.discardSomeReadBytes();
		}
	}

	public void setState(HandshakeState state) {
		this.state = state;
	}
	
	public Packet getDisconnectionPacket(Message m)
	{
		switch(this.state)
		{
			case LOGIN: return new PacketOutLoginPlayerDisconnect(m);
			case PLAY: return new PacketOutPlayerDisconnect(m);
		default:
			return null;
		}
	}
}
