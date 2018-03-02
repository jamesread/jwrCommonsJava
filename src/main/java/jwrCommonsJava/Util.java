/*
 * This file is part of jwrCommonsJava. jwrCommonsJava is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * jwrCommonsJava is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * jwrCommonsJava. If not, see <http://www.gnu.org/licenses/>.
 */

package jwrCommonsJava;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Util {
	public enum PropertyDesktopEnvironment {
		GNOME, KDE, UNKNOWN;

		public static PropertyDesktopEnvironment getCurrent() {
			String prop = System.getenv("KDE_SESSION_UID");

			if (prop != null) {
				return KDE;
			}

			return UNKNOWN;
		}
	}

	@Deprecated
	public enum PropertyJavaVersion {
		J2SE1_4, JAVA_SE_1_5, JAVA_SE_1_6, UNKNOWN;

		public static PropertyJavaVersion get() {
			return PropertyJavaVersion.get(System.getProperty("java.version"));
		}

		public static PropertyJavaVersion get(String javaVersion) {
			LOG.warn(javaVersion);

			if (javaVersion.startsWith("1.4")) {
				return J2SE1_4;
			}

			return UNKNOWN;
		}
	}

	public enum PropertyJavaVmVendor {
		SUN, UNKNOWN;

		public static PropertyJavaVmVendor getVmv() {
			final String vendor = System.getProperty("java.vm.vendor");

			if (vendor.contains("Sun Microsystems")) {
				return PropertyJavaVmVendor.SUN;
			}

			return PropertyJavaVmVendor.UNKNOWN;
		}
	}

	public enum PropertyOs {
		LIN, UNKNOWN, WIN;

		public static PropertyOs getOs() {
			final String os = System.getProperty("os.name");

			LOG.debug("OS: " + os);

			if (os.toLowerCase().contains("windows")) {
				return PropertyOs.WIN;
			} else if (os.toLowerCase().contains("linux")) {
				return PropertyOs.LIN;
			}

			return PropertyOs.UNKNOWN;
		}

		@Override
		public String toString() {
			switch (this) {
			case LIN:
				return "Linux";
			case WIN:
				return "Windows";
			case UNKNOWN:
			default:
				return "Unknown";
			}
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

	public static final String lineSeparator = System.getProperty("line.separator");

	public static long bytesToMegabytes(final int bytes) {
		return Util.bytesToMegabytes(bytes);
	}

	public static long bytesToMegabytes(final long bytes) {
		final long MEGABYTE = 1024L * 1024L;

		return bytes / MEGABYTE;
	}

	/**
	 * A simple helper function that cleans up code, JDom attributes return null
	 * if they do not exist and that could cause a lot of NPE's. This stops
	 * loads of if statements throughout the code and increases readability.
	 */
	public static String emptyIfNull(String message) {
		if (message == null) {
			message = "";
		}

		return message;
	}

	/**
	 * Gets a new GUID with the format: 234123-234.
	 * 
	 * @return
	 */
	public static String getGuid() {
		final long millis = System.currentTimeMillis();
		final Random rand = new Random();

		final String result = millis + "-" + rand.nextInt(255);

		return result;
	}

	/**
	 * This method is too complex to describe.
	 */
	public static String getLine() {
		return "----------------------------------------";
	}

	/**
	 * FIXME this can be cached quite easily.
	 * 
	 * @return
	 */
	public static ImageIcon getLogoImage() {
		final ImageIcon ii = new ImageIcon(Toolkit.getDefaultToolkit().getImage("../resources/images/logo.png"));
		return ii;
	}

	/**
	 * Sometimes, you just cannot be bothered to create the GBC _AND_ set its
	 * defaults. :)
	 * 
	 * @return
	 */
	public static GridBagConstraints getNewGbc() {
		return Util.setGbcDefaults(null);
	}

	public static int getPid() throws Exception {
		final String one = ManagementFactory.getRuntimeMXBean().getName();
		final String[] two = one.split("@");
		final Integer three = Integer.parseInt(two[0]);

		return three;
	}

	/**
	 * Gets a string that describes the platform. This should only be used in
	 * about dialogs and console outputs, etc.
	 * 
	 * If you need to get the info it returns, use the enum methods
	 * (PropertyOs.getOs(), etc). Do not try and parse it.
	 * 
	 * @return
	 */
	public static String getPlatformInfo() {
		final String version = System.getProperty("os.version");
		final String arch = System.getProperty("os.arch");
		final String os = PropertyOs.getOs().toString();
		final PropertyJavaVmVendor vmVendor = PropertyJavaVmVendor.getVmv();

		if (vmVendor == PropertyJavaVmVendor.UNKNOWN) {
			LOG.warn("The VM you are using cannot be detected.");
		}

		return os + "-" + version + "-" + arch + " on a " + vmVendor.toString() + " VM.";
	}

	public static String getSeparator() {
		return "--------------------------------------------------------------------------------";
	}

	public static String getSystemInfo() {
		String ret = "";
		ret += "Available processors: " + Runtime.getRuntime().availableProcessors() + " \r\n";

		ret += "Total memory: " + Util.bytesToMegabytes(Runtime.getRuntime().totalMemory()) + " MB\r\n";
		ret += "Max memory: " + Util.bytesToMegabytes(Runtime.getRuntime().maxMemory()) + " MB\r\n";
		ret += "Free memory: " + Util.bytesToMegabytes(Runtime.getRuntime().freeMemory()) + " MB\r\n";

		return ret;
	}

	public static final String implode(final List<? extends Object> set, final String glue) {
		return Util.implode(set, glue, glue);
	}

	public static final String implode(final List<? extends Object> set, final String glue, final String finalGlue) {
		String r = "";

		for (int i = 0; i < set.size(); i++) {
			if (i == 0) { // first
				r = set.get(i).toString();
			} else if ((i + 1) == set.size()) { // last
				r += finalGlue + set.get(i).toString();
			} else {
				r += glue + set.get(i).toString(); // in the middle
			}
		}

		return r;
	}

	public static final String implode(final Object[] objects, final String glue) {
		return Util.implode(Arrays.asList(objects), glue);
	}

	public static boolean isInJar() {
		final String className = Util.class.getName().replace('.', '/');
		final String classJar = Util.class.getResource("/" + className + ".class").toString();

		if (classJar.startsWith("jar:")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines if the application is running a stable/debug version.
	 * 
	 * @return If the minor version is an even number then the application is
	 *         stable (!). If the minor number cannot be determined then it
	 *         returns false anyway.
	 */
	public static boolean isStable() {
		final Pattern p = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)");
		final Matcher m = p.matcher(Configuration.getVersion());

		if (!m.matches()) {
			LOG.warn("Could not parse version number.");
			return false;
		}

		// final int major = Integer.parseInt(m.group(1));
		final int minor = Integer.parseInt(m.group(2));
		// final int revision = Integer.parseInt(m.group(3));

		return (minor % 2) == 0;
	}

	public static boolean isWindows() {
		return Util.PropertyOs.getOs() == PropertyOs.WIN;
	}

	/**
	 * Pads a string with spaces.
	 * 
	 * @param s
	 * @param length
	 * @return
	 */
	public static String pad(final String s, final long length) {
		return Util.pad(s, length, ' ');
	}

	/**
	 * Pads a string with an arbitatary character.
	 * 
	 * @param s
	 * @param length
	 * @param c
	 * @return
	 */
	public static String pad(String s, final long length, final char c) {
		while (s.length() <= length) {
			s += c;
		}

		return s;
	}

	public static Hashtable<String, String> parseIni(final BufferedReader br) {
		final Hashtable<String, String> entries = new Hashtable<String, String>();

		try {
			if (!br.ready()) {
				throw new IllegalStateException("The buffer is not ready to be read, it may be empty.");
			}

			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}

				// Serious suck when this line needs to be uncommented :(
				// LOG.debug(line);

				final String pair[] = line.split("=");

				if (pair.length != 2) {
					continue;
				}

				// System.out.println("Reading line: " + line);
				// System.out.println("cvar: " + pair[0] + "=" + pair[1]);
				entries.put(pair[0].trim(), pair[1].trim());
			}
		} catch (final Exception e) {
			LOG.error("Cannot parse INI.", e);
		}

		return entries;

	}

	public static Hashtable<String, String> parseIni(final File file) throws FileNotFoundException {

		if (!file.exists()) {
			throw new FileNotFoundException();
		}

		return Util.parseIni(new BufferedReader(new FileReader(file)));
	}

	/**
	 * A helper function to setup GBC's. The values set are purely for personal
	 * preference.
	 * 
	 * @param gbc
	 * @return
	 */
	public static GridBagConstraints setGbcDefaults(GridBagConstraints gbc) {
		/*
		 * This helps field init when using this function.
		 */
		if (gbc == null) {
			gbc = new GridBagConstraints();
		}

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.fill = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;

		return gbc;
	}

	public static GridBagConstraints setupLayout(JPanel con) {
		con.setLayout(new GridBagLayout());
		return Util.getNewGbc();
	}
}
