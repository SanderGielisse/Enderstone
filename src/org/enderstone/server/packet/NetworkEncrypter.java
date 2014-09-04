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
		int i = inbytes.readableBytes();
		byte[] arrayOfByte = fillInBuffer(inbytes);

		ByteBuf localByteBuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i));
		localByteBuf.writerIndex(this.cipher.update(arrayOfByte, 0, i, localByteBuf.array(), localByteBuf.arrayOffset()));

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

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String getHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] toCipherArray(Key key, byte[] byteArray) {
		try {
			return toCipherArray(2, key, byteArray);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			System.err.println("Cipher creation failed!");
			EnderLogger.exception(e);
		}
		return null;
	}

	private static byte[] toCipherArray(int arg0, Key cipherKey, byte[] byteArray) throws IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = toCipher(arg0, cipherKey.getAlgorithm(), cipherKey);
		return cipher.doFinal(byteArray);
	}

	private static Cipher toCipher(int arg0, String certificate, Key cipherKey) {
		try {
			Cipher newCipher = Cipher.getInstance(certificate);
			newCipher.init(arg0, cipherKey);
			return newCipher;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
			EnderLogger.exception(ex);
		}
		System.err.println("Cipher creation failed!");
		return null;
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] decode(String string, PublicKey publicKey, SecretKey secretKey){
		try {
			return toByteArray("SHA-1", new byte[][] { string.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded() });
		} catch (Exception e) {
			System.err.println("Cipher creation failed!");
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] toByteArray(String string, byte[][] byteArrayList) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(string);
		for (byte[] byteArray : byteArrayList) {
			messageDigest.update(byteArray);
		}
		return messageDigest.digest();
	}
}
