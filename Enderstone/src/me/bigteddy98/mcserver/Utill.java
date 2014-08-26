package me.bigteddy98.mcserver;

import me.bigteddy98.mcserver.entity.EnderPlayer;
import me.bigteddy98.mcserver.packet.play.PacketOutChatMessage;

public class Utill {

	public static void broadcastMessage(String message){
		PacketOutChatMessage packet = new PacketOutChatMessage(message, false);
		for(EnderPlayer player : Main.getInstance().onlinePlayers){
			player.getNetworkManager().sendPacket(packet);
		}
	}
}
