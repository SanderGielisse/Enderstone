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
package org.enderstone.server.uuid;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.json.JSONObject;

public class ServerRequest {

	private final String connectURL;

	public ServerRequest(String connectURL) {
		this.connectURL = connectURL;
	}

	public JSONObject get() throws IOException {
		return parseDataFromURL(connectURL);
	}

	public static JSONObject parseDataFromURL(String connect) throws IOException {
		URL url = new URL(connect);
		URLConnection uc = url.openConnection();
		uc.setConnectTimeout(5000);
		uc.setReadTimeout(5000);
		uc.connect();
		String encoding = uc.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		try (Scanner scanner = new Scanner(uc.getInputStream(),encoding)) {
			scanner.useDelimiter("\\A");
			return new JSONObject(scanner.next());
		}
		catch(NoSuchElementException noskin)
		{
			return new JSONObject();
		}
	}
}
