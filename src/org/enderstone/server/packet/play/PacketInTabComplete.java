package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Location;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

/**
 *
 * @author Fernando
 */
public class PacketInTabComplete extends Packet {

	private String halfCommand;
	private boolean hasPosition;
	private Location lookingAt;
	
	@Override
	public void read(ByteBuf buf) throws IOException {
		halfCommand = readString(buf);
		hasPosition = buf.readBoolean();
		if(hasPosition){
			lookingAt = readLocation(buf);
		}
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(halfCommand) + 1 + (hasPosition ? getLocationSize() : 0) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x14;
	}
	
	public String getHalfCommand() {
		return halfCommand;
	}
	
	@Override
	public void onRecieve(final NetworkManager networkManager) {
		networkManager.player.onPlayerChatComplete(this);
	}
	
}
