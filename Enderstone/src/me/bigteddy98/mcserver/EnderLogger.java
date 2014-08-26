package me.bigteddy98.mcserver;

import java.io.PrintWriter;
import java.io.StringWriter;
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
			@SuppressWarnings("deprecation")
			@Override
			public String format(LogRecord record) {

				StringBuilder sb = new StringBuilder();

				Date date = new Date(record.getMillis());
				sb.append(
						"[" + date.getDate() + "-" + (date.getMonth() + 1)
								+ "][" + date.getHours() + ":"
								+ date.getMinutes() + "]").append(" ")
						.append(record.getLevel().getLocalizedName())
						.append(": ").append(formatMessage(record))
						.append(System.lineSeparator());

				if (record.getThrown() != null) {
					try {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						record.getThrown().printStackTrace(pw);
						pw.close();
						sb.append(sw.toString());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				return sb.toString();
			}
		};
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

	public static void info(String info) {
		logger.log(Level.INFO, info);
	}

	public static void warn(String warning) {
		logger.log(Level.WARNING, warning);
	}
}
