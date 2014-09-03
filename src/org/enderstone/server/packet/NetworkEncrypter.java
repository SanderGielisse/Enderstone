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
import java.io.UnsupportedEncodingException;
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

	private final Cipher chipper;
	private byte[] decryptBuffer = new byte[0];
	private byte[] encryptBuffer = new byte[0];

	public NetworkEncrypter(Cipher chipper) {
		this.chipper = chipper;
	}

	private byte[] fillInBuffer(ByteBuf paramByteBuf) {
		int bytes = paramByteBuf.readableBytes();
		if (this.decryptBuffer.length < bytes) {
			this.decryptBuffer = new byte[bytes];
		}
		paramByteBuf.readBytes(this.decryptBuffer, 0, bytes);
		return this.decryptBuffer;
	}

	protected ByteBuf decrypt(ChannelHandlerContext ctx, ByteBuf inbytes) throws ShortBufferException {
		int i = inbytes.readableBytes();
		byte[] arrayOfByte = fillInBuffer(inbytes);

		ByteBuf localByteBuf = ctx.alloc().heapBuffer(this.chipper.getOutputSize(i));
		localByteBuf.writerIndex(this.chipper.update(arrayOfByte, 0, i, localByteBuf.array(), localByteBuf.arrayOffset()));

		return localByteBuf;
	}

	protected void encrypt(ByteBuf toEcrypt, ByteBuf output) throws ShortBufferException {
		int bytes = toEcrypt.readableBytes();
		byte[] arrayOfByte = fillInBuffer(toEcrypt);

		int j = this.chipper.getOutputSize(bytes);
		if (this.encryptBuffer.length < j) {
			this.encryptBuffer = new byte[j];
		}
		int encryptedSize = this.chipper.update(arrayOfByte, 0, bytes, this.encryptBuffer);
		output.writeBytes(this.encryptBuffer, 0, encryptedSize);
	}
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private static String javaHexDigest(String data) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(data.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			EnderLogger.exception(e);
			throw new Error("We cannot run a minecraft server under this platform!", e);
		}
		byte[] hash = digest.digest();
		boolean negative = (hash[0] & 0x80) == 0x80;
		if (negative)
			hash = twosCompliment(hash);
		String digests = getHexString(hash);
		if (digests.startsWith("0")) {
			digests = digests.replaceFirst("0", digests);
		}
		if (negative) {
			digests = "-" + digests;
		}
		digests = digests.toLowerCase();
		return digests;
	}

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

	private static byte[] twosCompliment(byte[] p) {
		int i;
		boolean carry = true;
		for (i = p.length - 1; i >= 0; i--) {
			p[i] = (byte) ~p[i];
			if (carry) {
				carry = p[i] == 0xFF;
				p[i]++;
			}
		}
		return p;
	}

	public static byte[] b(Key paramKey, byte[] paramArrayOfByte) {
		return a(2, paramKey, paramArrayOfByte);
	}

	private static byte[] a(int paramInt, Key paramKey, byte[] paramArrayOfByte) {
		try {
			return a(paramInt, paramKey.getAlgorithm(), paramKey).doFinal(paramArrayOfByte);
		} catch (IllegalBlockSizeException | BadPaddingException ex) {
			EnderLogger.exception(ex);
		}
		System.err.println("Cipher data failed!");
		return null;
	}

	private static Cipher a(int paramInt, String paramString, Key paramKey) {
		try {
			Cipher localCipher = Cipher.getInstance(paramString);
			localCipher.init(paramInt, paramKey);
			return localCipher;
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

	public static byte[] a(String paramString, PublicKey paramPublicKey, SecretKey paramSecretKey) {
		try {
			return a("SHA-1", new byte[][]{paramString.getBytes("ISO_8859_1"), paramSecretKey.getEncoded(), paramPublicKey.getEncoded()});
		} catch (UnsupportedEncodingException ex) {
			EnderLogger.exception(ex);
		}

		return null;
	}

	private static byte[] a(String paramString, byte[][] paramArrayOfByte) {
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
			for (byte[] arrayOfByte1 : paramArrayOfByte) {
				localMessageDigest.update(arrayOfByte1);
			}
			return localMessageDigest.digest();
		} catch (NoSuchAlgorithmException ex) {
			EnderLogger.exception(ex);
		}
		return null;
	}
}
