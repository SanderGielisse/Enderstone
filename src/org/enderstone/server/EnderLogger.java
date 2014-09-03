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
package org.enderstone.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class EnderLogger {

	public static final Logger logger = Logger.getLogger(Main.NAME);

	static {
		logger.setUseParentHandlers(false);
		Formatter formatter = new Formatter() {
			@Override
			public String format(LogRecord record) {
				StringBuilder sb = new StringBuilder();
				SimpleDateFormat dt1 = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
				sb.append(dt1.format(new Date())).append(record.getLevel().getLocalizedName()).append(": ").append(formatMessage(record)).append(System.lineSeparator());
				@SuppressWarnings("ThrowableResultIgnored")
				Throwable exception = record.getThrown();
				if (exception != null) {
					try {
						StringWriter sw = new StringWriter();
						try (PrintWriter pw = new PrintWriter(sw)) {
							exception.printStackTrace(pw);
						}
						sb.append(sw.toString());
					} catch (Exception ex) {
						ex.addSuppressed(exception);
						ex.printStackTrace();
					}
				}

				return sb.toString();
			}
		};
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
	}

	public static void info(String info) {
		logger.log(Level.INFO, info);
	}

	public static void warn(String warning) {
		logger.log(Level.WARNING, warning);
	}

	public static void error(String error) {
		logger.log(Level.SEVERE, error);
	}

	public static void exception(Throwable error) {
		logger.log(Level.SEVERE, "Error: " + error.toString(), error);
	}

	public static void debug(String message) {
		logger.log(Level.FINE, message);
	}
}
