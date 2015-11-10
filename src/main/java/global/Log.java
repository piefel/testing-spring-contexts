package global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Piefel
 * @since 10/11/2015
 */
public class Log {

	private static final StringBuffer BUFFER = new StringBuffer();
	public static final Logger LOGGER = LoggerFactory.getLogger(Log.class);

	public static StringBuffer append(String str) {
		return BUFFER.append(str);
	}

	public static void log() {
		LOGGER.info(BUFFER.toString());
		BUFFER.setLength(0);
	}

	public static void init(String s) {
		if (BUFFER.length() > 0)
			LOGGER.warn("was: {}", BUFFER.toString());
		System.out.println();
		BUFFER.setLength(0);
		BUFFER.append(s);
		BUFFER.append(": ");
	}
}
