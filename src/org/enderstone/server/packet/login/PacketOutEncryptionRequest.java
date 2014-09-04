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
package org.enderstone.server.packet.login;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.security.PublicKey;
import org.enderstone.server.packet.Packet;

/**
 *
 * @author Fernando
 */
public class PacketOutEncryptionRequest extends Packet {

	String serverid;
	PublicKey publicKey;
	byte[] publicKeyBytes;
	int verifyTokenSize;
	byte[] verifyToken;

	public PacketOutEncryptionRequest(String serverid, PublicKey publicKey, int verifyTokenSize, byte[] verifyToken) {
		this.serverid = serverid;
		this.publicKey = publicKey;
		this.verifyTokenSize = verifyTokenSize;
		this.verifyToken = verifyToken;
	}

	public PacketOutEncryptionRequest(String serverid, PublicKey publicKey, byte[] verifyToken) {
		this(serverid, publicKey, verifyToken.length, verifyToken);
	}

	@Override
	public void read(ByteBuf buf) throws IOException {
		throw new UnsupportedOperationException("Cannot read outgoing packet");
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		if (publicKeyBytes == null) publicKeyBytes = publicKey.getEncoded();
		writeString(serverid, buf);
		writeVarInt(publicKeyBytes.length, buf);
		buf.writeBytes(publicKeyBytes, 0, publicKeyBytes.length);
		writeVarInt(verifyTokenSize, buf);
		buf.writeBytes(verifyToken, 0, verifyTokenSize);
	}

	@Override
	public int getSize() throws IOException {
		if (publicKeyBytes == null) publicKeyBytes = publicKey.getEncoded();
		int size = 0;
		size += getStringSize(serverid);
		size += getVarIntSize(publicKeyBytes.length);
		size += publicKeyBytes.length;
		size += getVarIntSize(verifyTokenSize);
		size += verifyTokenSize;
		return size + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x01;
	}

}
