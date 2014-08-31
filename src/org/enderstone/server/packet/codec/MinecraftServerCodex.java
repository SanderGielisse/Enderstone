package org.enderstone.server.packet.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.io.IOException;
import java.util.List;
import org.enderstone.server.packet.HandshakeState;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.PacketHandshake;
import org.enderstone.server.packet.PacketManager;

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
		packet.writeFully(buf);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws DecodeException {
		//System.out.println("Minecratservercodex.decode");
		while (buf.readableBytes() > 0) {
			buf.markReaderIndex();
			int readBytes = buf.readableBytes();
			int length = 0;
			{
				int bytes = 0;
				byte in;
				while (true) {
					if (readBytes < 1) {
						//System.out.println("cannot read fully length marker");
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
				//System.out.println("cannot read full packet");
				buf.resetReaderIndex();
				return;
			}
			int id = Packet.readVarInt(buf);
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
							throw new IOException("Packet decode failed:"
									+ "\nPacket ID: 0x" + Integer.toHexString(id) + ""
									+ "\nHandshake state: " + this.state
									+ "\nPacket size: " + length
									+ "\nReason: Unknown packet");
						packet.read(buf);
						in = packet;
					}
					break;
				}
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
}
