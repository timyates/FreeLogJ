/* Copyright (c) 2021, Chris Newland (@chriswhocodes)

if (you need a fully featured logging framework)
{
      Take some time to understand its feature set (including the security risks of unused features)
      Start a conversation in your company about supporting open source maintainers!
}
else
{
      Enjoy this tiny and simple logging class!
}
 */

package com.chrisnewland.freelogj;

import java.io.PrintStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class Logger
{
	public enum LogLevel
	{
		TRACE(" TRC ", 0), DEBUG(" DBG ", 1), INFO(" INF ", 2), WARN(" WRN ", 3), ERROR(" ERR ", 4), FATAL(" FTL ", 5);

		final String display;
		private final int level;

		LogLevel(String display, int level)
		{
			this.display = display;
			this.level = level;
		}
	}

	static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ISO_INSTANT;

	private static final String BRACES = "{}";

	private final String className;

	private final LogLevel logLevel;

	private final DateTimeFormatter dateFormat;

	private final PrintStream printStream;

	public static Logger getLogger(Class<?> clazz)
	{
		return new Logger(clazz, LogLevel.INFO, DEFAULT_DATE_FORMAT, System.out);
	}

	public static Logger getLogger(Class<?> clazz, LogLevel logLevel)
	{
		return new Logger(clazz, logLevel, DEFAULT_DATE_FORMAT, System.out);
	}

	public static Logger getLogger(Class<?> clazz, LogLevel logLevel, PrintStream printStream)
	{
		return new Logger(clazz, logLevel, DEFAULT_DATE_FORMAT, printStream);
	}

	public static Logger getLogger(Class<?> clazz, LogLevel logLevel, DateTimeFormatter dateFormat, PrintStream printStream)
	{
		return new Logger(clazz, logLevel, dateFormat, printStream);
	}

	private Logger(Class<?> clazz, LogLevel logLevel, DateTimeFormatter dateFormat, PrintStream printStream)
	{
		this.className = clazz.getName();
		this.logLevel = logLevel;
		this.dateFormat = dateFormat;
		this.printStream = printStream;
	}

	public boolean isTraceEnabled()
	{
		return this.logLevel.level <= LogLevel.TRACE.level;
	}

	public boolean isDebugEnabled()
	{
		return this.logLevel.level <= LogLevel.DEBUG.level;
	}

	public boolean isInfoEnabled()
	{
		return this.logLevel.level <= LogLevel.INFO.level;
	}

	public boolean isWarnEnabled()
	{
		return this.logLevel.level <= LogLevel.WARN.level;
	}

	public boolean isErrorEnabled()
	{
		return this.logLevel.level <= LogLevel.ERROR.level;
	}

	public boolean isFatalEnabled()
	{
		return true;
	}

	public void trace(String message, Object... args)
	{
		log(LogLevel.TRACE, message, args);
	}

	public void debug(String message, Object... args)
	{
		log(LogLevel.DEBUG, message, args);
	}

	public void info(String message, Object... args)
	{
		log(LogLevel.INFO, message, args);
	}

	public void warn(String message, Object... args)
	{
		log(LogLevel.WARN, message, args);
	}

	public void error(String message, Object... args)
	{
		log(LogLevel.ERROR, message, args);
	}

	public void fatal(String message, Object... args)
	{
		log(LogLevel.FATAL, message, args);
	}

	private void log(LogLevel logLevel, String message, Object... args)
	{
		if (logLevel.level < this.logLevel.level)
		{
			return;
		}

		StringBuilder builder = new StringBuilder();

		builder.append(dateFormat.format(Instant.now()));

		builder.append(logLevel.display).append(className).append(' ');

		if (message != null && args != null && args.length > 0)
		{
			int messageLength = message.length();

			int messagePos = 0;
			int bracesPos = 0;

			int argPos = 0;

			while ((bracesPos = message.indexOf(BRACES, bracesPos)) != -1 && argPos < args.length)
			{
				builder.append(message, messagePos, bracesPos);
				builder.append(args[argPos++]);

				bracesPos += 2;
				messagePos = bracesPos;
			}

			if (messagePos < messageLength)
			{
				builder.append(message, messagePos, messageLength);
			}

			printStream.println(builder);

			// if there are remaining args that are Throwable then print stack traces
			if (argPos < args.length)
			{
				for (int i = argPos; i < args.length; i++)
				{
					if (args[i] instanceof Throwable)
					{
						((Throwable) args[i]).printStackTrace(printStream);
					}
				}
			}
		}
		else
		{
			builder.append(message);
			printStream.println(builder);
		}
	}
}