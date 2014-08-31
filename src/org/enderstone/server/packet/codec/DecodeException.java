
package org.enderstone.server.packet.codec;

import java.io.IOException;

/**
 *
 * @author Fernando
 */
public class DecodeException extends IOException {

	public DecodeException() {
	}

	public DecodeException(String msg) {
		super(msg);
	}

	public DecodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DecodeException(Throwable cause) {
		super(cause);
	}
}
