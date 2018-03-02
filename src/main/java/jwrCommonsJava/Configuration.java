package jwrCommonsJava;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.slf4j.LoggerFactory;

/**
 * A rather awesome Configuration class.
 * 
 * It is recommended that wrappers are built around this using the Proxy oodp.
 */
public abstract class Configuration {
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Configuration.class);

	private static String cachedCfgDir = null;

	private static final HashMap<String, String> cvars = new HashMap<String, String>();

	protected static final String[] protectedCvars = { "version", "sysAppName" };

	static {
		try {
			LOG.debug("Initializing configuration class (staticly)");

			Configuration.iset("version", "?.?.?");
			Configuration.iset("sysAppName", "jwrCommons-DefaultApp");

			Arrays.sort(Configuration.protectedCvars);

			LOG.debug("isInJar: " + Util.isInJar());
		} catch (final Exception e) {
			LOG.error("Unhandled exception in configuration initializer.", e);
		}
	}

	public static final void clearAll() {
		Configuration.cvars.clear();
	}

	public static final void createUserConfig() throws Exception {
		new File(Configuration.getConfigDir()).mkdirs();
		new File(Configuration.getConfigDir() + File.separator + "configuration.ini").createNewFile();
	}

	/**
	 * @deprecated Use the type based methods.
	 * @param key
	 * @return The value of the configuration key.
	 */
	@Deprecated
	public static final String get(final String key) {
		return Configuration.cvars.get(key);
	}

	public static final HashMap<String, String> getAll() {
		return Configuration.cvars;
	}

	public final static boolean getB(final String key) {
		return Boolean.parseBoolean(Configuration.cvars.get(key));
	}

	public static Rectangle getBounds(String windowName, int x, int y, int w, int h) {
		return new Rectangle(Configuration.getI(windowName + ".x", x), Configuration.getI(windowName + ".y", y), Configuration.getI(windowName + ".w", w), Configuration.getI(windowName + ".h", h));
	}

	public static Color getColor(final String key, Color defaultColor) {
		int r = Configuration.getI(key + ".r", defaultColor.getRed());
		int g = Configuration.getI(key + ".g", defaultColor.getGreen());
		int b = Configuration.getI(key + ".b", defaultColor.getBlue());

		return new Color(r, g, b);
	}

	public static final String getConfigDir() {
		if (Configuration.cachedCfgDir == null) {
			Configuration.cachedCfgDir = Configuration.getUserConfigDir();
		}

		return Configuration.cachedCfgDir;
	}

	public static final double getD(final String key) {
		return Double.parseDouble(Configuration.cvars.get(key));
	}

	public static final float getF(final String key) {
		return Float.parseFloat(Configuration.cvars.get(key));
	}

	public static final int getI(final String key) {
		return Integer.parseInt(Configuration.cvars.get(key));
	}

	public static final int getI(final String key, int def) {
		try {
			return Integer.parseInt(Configuration.cvars.get(key));
		} catch (Exception e) {
			return def;
		}
	}

	public static final String getS(final String key) {
		if (Configuration.cvars.containsKey(key)) {
			return Configuration.cvars.get(key);
		} else {
			return "";
		}
	}

	public static final String getTable() {
		String ret = "";

		ret += Util.pad("Key", 20) + " | " + Util.pad("Value", 20) + "\n";
		ret += Util.getLine() + "\n";

		Vector<String> keys = new Vector<String>(Configuration.cvars.keySet());
		Collections.sort(keys);

		for (String k : keys) {
			ret += Util.pad(k.toString(), 20) + " | " + Configuration.cvars.get(k) + "\n";
		}

		return ret;
	}

	public static final String getUserConfigDir() {
		switch (Util.PropertyOs.getOs()) {
		case LIN:
			return System.getProperty("user.home") + "/." + Configuration.getS("sysAppName") + "/";
		case WIN:
			final float ver = Float.parseFloat(System.getProperty("os.version"));

			if (ver < 6.0) {
				return System.getProperty("user.home") + "\\Application Data\\" + Configuration.getS("sysAppName") + "\\";
			} else if (ver >= 6.0) {
				return System.getProperty("user.home") + "\\AppData\\Roaming\\" + Configuration.getS("sysAppName") + "\\";
			} else {
				return System.getProperty("user.home") + "\\AppData\\Roaming\\" + Configuration.getS("sysAppName") + "\\";
			}
		default:
			return System.getProperty("user.home" + "\\" + Configuration.getS("sysAppName") + "\\");
		}
	}

	public static final String getVersion() {
		return Configuration.cvars.get("version");
	}

	/**
	 * The "internal set" method. The public methods call this one, which does
	 * all the boiler plate stuff irrespective of data type.
	 * 
	 * @param key
	 * @param value
	 *            A string representation of the cvar value.
	 */
	private static final void iset(final String key, final String value) {
		/*
		 * Phew, check this one out.
		 * 
		 * Default keys have a ? at the start, like "?.?.?" for version or "???"
		 * for other unstructured values. Oh, and "jwr" is counted.
		 */
		if (Configuration.isProtected(key)) {
			if ((Configuration.cvars.get(key) == null) || (Configuration.cvars.get(key).charAt(0) == '?') || Configuration.cvars.get(key).startsWith("jwr")) {
				LOG.debug("Set of protected cvar: \"" + key + "\" as it's current value is a bit special.");
			} else {
				LOG.warn("Disallowing set of protected cvar: \"" + key + "\" to \"" + value + "\".");
				return;
			}
		}

		Configuration.cvars.put(key, value);
	}

	public static final boolean isKeyExistering(final String k) {
		return Configuration.cvars.containsKey(k);
	}

	private static final boolean isProtected(final String key) {
		return Arrays.binarySearch(Configuration.protectedCvars, key) >= 0;
	}

	public static final boolean isUserConfigExistering() {
		return new File(Configuration.getConfigDir()).exists();
	}

	private static final void parseConfigurationFile(final BufferedReader br) {
		Hashtable<String, String> cvars = Util.parseIni(br);

		for (String key : cvars.keySet()) {
			Configuration.iset(key, cvars.get(key));
		}
	}

	public static final void parseInternalConfiguration() {
		if (Util.isInJar()) {
			LOG.debug("Parsing internal configuration.");

			if (Configuration.isKeyExistering("internalConfigurationPath")) {
				Configuration.parseConfigurationFile(JarUtil.getAsciiFile(Configuration.getS("internalConfigurationPath")));
			} else {
				Configuration.parseConfigurationFile(JarUtil.getAsciiFile("configuration.ini"));
			}
		} else {
			LOG.debug("Not parsing internal configuration, we're probably not in a jar.");
		}

	}

	/**
	 * Will parse the external configuration file if it is found in the default
	 * place.
	 */
	public static final void parseUserConfiguration() {
		LOG.debug("Parsing external configuration.");

		final File cfgFile = new File(Configuration.getConfigDir() + "configuration.ini");

		if (!cfgFile.exists()) {
			LOG.debug("External configuration does not exist, skipping.");
			return;
		} else {
			LOG.debug("External configuration found at: " + cfgFile.getAbsolutePath());
		}

		if (cfgFile.length() == 0) {
			LOG.info("External configuration exists, but is empty. Wont bother parsing.");
			return;
		}

		try {
			Configuration.parseConfigurationFile(new BufferedReader(new FileReader(cfgFile)));
		} catch (final FileNotFoundException e) {
			LOG.debug("External configuration file not found in default path (" + Configuration.getConfigDir() + ")");
		}
	}

	public static final void printDebugTable() {
		LOG.debug(Util.pad("Key", 50) + "|" + Util.pad(" Value", 20));

		for (final Map.Entry<String, String> e : Configuration.cvars.entrySet()) {
			final String k = e.getKey();
			final String v = e.getValue();

			LOG.debug(Util.pad(k, 50) + "| " + Util.pad(v, 20));
		}
	}

	/**
	 * Save the configuration file under the default location.
	 */
	public static final void save() {
		final File cfgFile = new File(Configuration.getConfigDir() + "configuration.ini");

		Configuration.save(cfgFile);
	}

	public static final void save(final File f) {
		LOG.debug("Saving configuration to: " + f.getPath());
		String output = "# Generated: ???" + Util.lineSeparator;

		for (final Map.Entry<String, String> e : Configuration.cvars.entrySet()) {
			final String k = e.getKey();
			final String v = e.getValue();

			if (!Configuration.isProtected(k)) {
				output += k + "=" + v + Util.lineSeparator;
			}
		}

		try {
			final FileWriter fw = new FileWriter(f);
			fw.write(output);
			fw.close();
		} catch (final Exception e) {
			LOG.error("Could not write CFG file!", e);
		}
	}

	public static final void set(final String k, final Boolean b) {
		Configuration.iset(k, ((b == null) ? "" : b.toString()));
	}

	public static final void set(final String k, final Double d) {
		Configuration.iset(k, d.toString());
	}

	public static final void set(final String k, final Float f) {
		Configuration.iset(k, f.toString());
	}

	public static final void set(final String k, final Integer i) {
		Configuration.iset(k, i.toString());
	}

	public static final void set(final String k, final String s) {
		Configuration.iset(k, s);
	}

	public static void setBounds(String windowName, Rectangle bounds) {
		Configuration.set(windowName + ".x", bounds.x);
		Configuration.set(windowName + ".y", bounds.y);
		Configuration.set(windowName + ".w", bounds.width);
		Configuration.set(windowName + ".h", bounds.height);
	}

	public static void setColor(final String key, Color c) {
		Configuration.set(key + ".r", c.getRed());
		Configuration.set(key + ".g", c.getGreen());
		Configuration.set(key + ".b", c.getBlue());
	}

	public static final boolean tableIsEmpty() {
		return Configuration.cvars.size() == 0;
	}
}