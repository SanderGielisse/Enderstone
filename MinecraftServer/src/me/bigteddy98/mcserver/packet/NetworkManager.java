package me.bigteddy98.mcserver.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.bigteddy98.mcserver.Main;
import me.bigteddy98.mcserver.entity.EnderPlayer;
import me.bigteddy98.mcserver.packet.NetworkManager.Stage;
import me.bigteddy98.mcserver.packet.login.PacketInLoginStart;
import me.bigteddy98.mcserver.packet.login.PacketOutLoginSucces;
import me.bigteddy98.mcserver.packet.play.PacketInClientSettings;
import me.bigteddy98.mcserver.packet.play.PacketInPlayerPositionLook;
import me.bigteddy98.mcserver.packet.play.PacketInPluginMessage;
import me.bigteddy98.mcserver.packet.play.PacketKeepAlive;
import me.bigteddy98.mcserver.packet.play.PacketOutJoinGame;
import me.bigteddy98.mcserver.packet.play.PacketOutPlayerAbilities;
import me.bigteddy98.mcserver.packet.play.PacketOutPlayerPositionLook;
import me.bigteddy98.mcserver.packet.play.PacketOutPluginMessage;
import me.bigteddy98.mcserver.packet.play.PacketOutSpawnPosition;
import me.bigteddy98.mcserver.packet.status.PacketPing;
import me.bigteddy98.mcserver.packet.status.PacketInRequest;
import me.bigteddy98.mcserver.packet.status.PacketOutResponse;

public class NetworkManager extends ReplayingDecoder<Stage> {

	private Channel channel;
	EnderPlayer player;
	private PacketReciever packetReciever;

	int handShakeStatus = -1;
	private int length;
	private List<Packet> waitingPackets = Collections.synchronizedList(new ArrayList<Packet>());

	public NetworkManager() {
		super(Stage.LENGTH);
		this.packetReciever = new PacketReciever(this);
	}

	public void sendPacket(Packet packet) {
		synchronized (waitingPackets) {
			this.waitingPackets.add(packet);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> args) throws Exception {

		if (channel == null) {
			channel = ctx.channel();
		}

		if (this.state() == Stage.LENGTH) {
			this.length = Packet.readVarInt(buf);
			this.checkpoint(Stage.DATA);
		} else if (this.state() == Stage.DATA) {
			buf.markReaderIndex();
			buf.readBytes(this.length);
			buf.resetReaderIndex();

			int id = Packet.readVarInt(buf);

			if (handShakeStatus == -1) {
				PacketHandshake packet = (PacketHandshake) PacketManager.handshake.newInstance();
				packet.read(buf);
				this.handShakeStatus = packet.getNextState();
			} else if (handShakeStatus == 1) {
				Packet packet = PacketManager.getPacket(this, id, HandshakeState.STATUS).newInstance();
				packet.read(buf);
				this.onPacketRecieve(packet);
			} else if (handShakeStatus == 2) {
				Packet packet = PacketManager.getPacket(this, id, HandshakeState.LOGIN).newInstance();
				packet.read(buf);
				this.onPacketRecieve(packet);
			} else if (handShakeStatus == 3) {
				Packet packet = PacketManager.getPacket(this, id, HandshakeState.PLAY).newInstance();
				packet.read(buf);
				this.onPacketRecieve(packet);
			}
			this.state(Stage.LENGTH);

			synchronized (waitingPackets) {
				this.writeAndFlushPackets(waitingPackets.toArray(new Packet[0]));
				this.waitingPackets.clear();
			}
		}
	}

	private void onPacketRecieve(Packet packet) throws Exception {

		// System.out.println("Recieved " + packet.getClass().getSimpleName() +
		// " with id 0x" + Integer.toHexString(packet.getId()));

		if (packet instanceof PacketInRequest) {
			this.packetReciever.packetInRequest((PacketInRequest) packet);
		} else if (packet instanceof PacketPing) {
			this.packetReciever.packetPing((PacketPing) packet);
		} else if (packet instanceof PacketInLoginStart) {
			//this.packetReciever.packet
		} else if (packet instanceof PacketInClientSettings) {
			//
		} else if (packet instanceof PacketInPluginMessage) {
			PacketInPluginMessage message = (PacketInPluginMessage) packet;

			if (message.getChannel().equals("REGISTER")) {
				// REGISTER.add(new String(message.getData(), "UTF-8"));
			} else if (message.getChannel().equals("UNREGISTER")) {
				// REGISTER.remove(new String(message.getData(), "UTF-8"));
			} else if (message.getChannel().equals("MC|Brand")) {
				this.sendPacket(new PacketOutPluginMessage(message.getChannel(), message.getLength(), message.getData()));
			}
		} else if (packet instanceof PacketInPlayerPositionLook) {
			//
		} else if (packet instanceof PacketKeepAlive) {
			// this.sendPacket(packet);
		}
	}

	private void writeAndFlushPackets(Packet... packets) throws Exception {

		ByteBuf buf = Unpooled.buffer();
		for (Packet packet : packets) {
			Packet.writeVarInt(packet.getSize(), buf);
			Packet.writeVarInt(packet.getId(), buf);
			packet.write(buf);
		}
		this.channel.writeAndFlush(buf);
	}

	enum Stage {
		LENGTH, DATA
	}
}
