package jwrCommonsJava;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JarUtil.class);

	public static BufferedReader getAsciiFile(final String filename) {
		if (filename.startsWith("/")) {
			throw new IllegalArgumentException("You don't need a preceeding slash.");
		}

		// Ya, it's fooking cheeky :)
		final BufferedReader br = new BufferedReader(new InputStreamReader(JarUtil.getResource(filename)));

		return br;
	}

	@Deprecated
	public static InputStream getBinaryFileOld(final String filename) {
		if (!filename.startsWith("/")) {
			throw new IllegalArgumentException("The filename should start with a \"/\".");
		}

		InputStream is = null;

		try {
			is = JarUtil.class.getResourceAsStream(filename);

			if (is == null) {
				throw new FileNotFoundException(filename);
			}

			return is;
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (final NullPointerException npe) {
				// ignore
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static InputStream getResource(final String filename) {
		try {
			LOG.debug("getResourceAsStream: " + filename);

			final InputStream is = JarUtil.class.getResourceAsStream("/" + filename);

			if (is == null) {
				throw new FileNotFoundException(filename);
			}

			LOG.debug("Loaded resource \"" + filename + "\" from jar");

			return is;
		} catch (final NullPointerException e) {
			LOG.debug("Could not load resource \"" + filename + "\" from jar!");
		} catch (final Exception e) {
			LOG.debug("Could not load resource \"" + filename + "\" from jar!");
		}

		return null;
	}

	public static boolean isInAJar() {
		return JarUtil.isInAJar(JarUtil.class);
	}

	public static boolean isInAJar(final Class<?> c) {
		return c.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar");
	}
}
