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
package org.enderstone.server.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import org.enderstone.server.EnderLogger;

/**
 *
 * @author Fernando
 */
public class NetworkEncrypter {

	private final Cipher cipher;
	private byte[] decryptBuffer = new byte[0];
	private byte[] encryptBuffer = new byte[0];

	public NetworkEncrypter(Cipher cipher) {
		this.cipher = cipher;
	}

	private byte[] fillInBuffer(ByteBuf buffer) {
		int bytes = buffer.readableBytes();
		if (this.decryptBuffer.length < bytes) {
			this.decryptBuffer = new byte[bytes];
		}
		buffer.readBytes(this.decryptBuffer, 0, bytes);
		return this.decryptBuffer;
	}

	protected ByteBuf decrypt(ChannelHandlerContext ctx, ByteBuf inbytes) throws ShortBufferException {
		int readableBytes = inbytes.readableBytes();
		byte[] byteArray = fillInBuffer(inbytes);
		ByteBuf localByteBuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(readableBytes));
		localByteBuf.writerIndex(this.cipher.update(byteArray, 0, readableBytes, localByteBuf.array(), localByteBuf.arrayOffset()));
		return localByteBuf;
	}

	protected void encrypt(ByteBuf toEcrypt, ByteBuf output) throws ShortBufferException {
		int bytes = toEcrypt.readableBytes();
		byte[] arrayOfByte = fillInBuffer(toEcrypt);

		int j = this.cipher.getOutputSize(bytes);
		if (this.encryptBuffer.length < j) {
			this.encryptBuffer = new byte[j];
		}
		int encryptedSize = this.cipher.update(arrayOfByte, 0, bytes, this.encryptBuffer);
		output.writeBytes(this.encryptBuffer, 0, encryptedSize);
	}

	public static byte[] toCipherArray(Key key, byte[] array) {
		try {
			Cipher newCipher = Cipher.getInstance(key.getAlgorithm());
			newCipher.init(2, key);
			return newCipher.doFinal(array);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			EnderLogger.exception(e);
			System.err.println("Cipher creation failed!");
		}
		return null;
	}

	public static byte[] decode(String string, PublicKey publicKey, SecretKey secretKey){
		try {
			byte[][] multiDimensionalArray = new byte[][] { string.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded() };
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			for (byte[] byteArray : multiDimensionalArray) {
				messageDigest.update(byteArray);
			}
			return messageDigest.digest();
		} catch (Exception e) {
			System.err.println("Cipher creation failed!");
			e.printStackTrace();
		}
		return null;
	}
}
