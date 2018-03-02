package jwrCommonsJava;

import java.util.Vector;

import org.slf4j.LoggerFactory;

@Deprecated
public class Logger {
	public static interface Listener {
		public void fireNewMessage(String message, MessageType mt);
	}

	public static enum MessageType {
		DEBUG, EASTEREGG, ERROR, EXCEPTION, NORMAL, WARNING
	}

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

	private static Vector<Listener> listeners = new Vector<Listener>();

	public static boolean useSlf4j = false;

	public static void addListener(final Listener l) {
		Logger.listeners.add(l);
	}

	public static void debugMarker() {
		logger.debug("<marker>");
	}

	public static void messageDebug(final String message) {
		logger.debug(message);
	}

	public static void messageDebugLine() {
		logger.debug("-------------------------------------------------------------");

	}

	public static void messageEasterEgg(final String message) {
		logger.warn(message);
	}

	public static void messageError(final Error e) {
		logger.error(e.toString());

		System.exit(1);
	}

	public static void messageError(final Error e, final String description) {
		logger.error(e.toString() + "###" + description);

		System.exit(1);
	}

	public static void messageError(final Error e, final String description, final String title, final boolean popup) {
		logger.error(title + "###" + description + "###" + e.getMessage());

		System.exit(1);
	}

	/**
	 * Prints an error and exits the program.
	 * 
	 * @param message
	 *            The error message.
	 */
	public static void messageError(final String message) {
		logger.error(message);

		System.exit(1);
	}

	/**
	 * @deprecated Exceptions should not be fatal.
	 */
	@Deprecated
	public static void messageException(final Exception e, final String description, final boolean fatal) {
		logger.error(e.toString());

		e.printStackTrace();
	}

	public static void messageException(final Exception e, final String description, final String title, final boolean popup) {
		logger.error(e.toString() + "###" + description);
	}

	public static void messageException(final InterruptedException e) {
		logger.warn(e.getMessage());
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static void messageException(final String message) {
		logger.warn(message);
	}

	public static void messageException(final Throwable e) {
		logger.warn(e.toString());

		e.printStackTrace();
	}

	public static void messageException(final Throwable thown, final String description) {
		logger.debug(description);

		thown.printStackTrace();
	}

	public static void messageNormal(final String message) {
		logger.info(message);
	}

	public static void messageNormalLine() {
		logger.info("----------------------------------------------");
	}

	public static void messageSeparator() {
		messageNormalLine();
	}

	public static void messageWarning(final String message) {
		logger.warn(message);
	}

	public static void removeListener(final Listener l) {
		Logger.listeners.remove(l);
	}

	public static void setMessageTypeEnabled(final MessageType mt) {

	}

}
