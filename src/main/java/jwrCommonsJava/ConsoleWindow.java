package jwrCommonsJava;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleWindow extends JFrame implements KeyListener {
	private class CommandList extends Hashtable<String, Command> {
		public void add(final Command c) {
			this.put(c.getFirstPart(), c);
		}
	}

	public class ResultsBuffer {
		private final StringBuilder sb = new StringBuilder("");

		public void append(final String message) {
			this.sb.append(message);
		}

		public String get() {
			return this.sb.toString();
		}
	}

	private class ResultsPane extends JTextPane {
		final Style styleInput;
		final Style styleOutput;

		public ResultsPane() {
			StyledDocument sdoc = (StyledDocument) this.getDocument();
			this.styleInput = sdoc.addStyle("input", null);
			StyleConstants.setForeground(this.styleInput, Color.BLUE);

			this.styleOutput = sdoc.addStyle("output", null);
			StyleConstants.setForeground(this.styleOutput, Color.BLACK);
		}

		public void append(Style style, String string) {
			try {
				this.getDocument().insertString(this.getDocument().getLength(), string, style);
			} catch (BadLocationException e) {
				LOG.error("BadLocationException", e);
			}
		}

		public void appendInput(String str) {
			this.append(this.styleInput, str);
		}

		public void appendOutput(String str) {
			this.append(this.styleOutput, str);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ConsoleWindow.class);

	public final static ConsoleWindow instance = new ConsoleWindow();
	private final CommandList cl = new CommandList();
	private String lastCommand = "";
	private final JTextField txtEntry = new JTextField();
	private final ResultsPane txtResults = new ResultsPane();

	private ConsoleWindow() {
		this.setupStandardCommands();

		this.setLayout(new BorderLayout());

		this.txtResults.setFont(Font.getFont(Font.MONOSPACED));
		this.txtResults.setEditable(false);
		this.txtResults.setAutoscrolls(true);
		this.add(new JScrollPane(this.txtResults), BorderLayout.CENTER);
		this.txtEntry.addKeyListener(this);
		this.add(this.txtEntry, BorderLayout.SOUTH);
		this.setBounds(100, 100, 360, 200);
		this.setTitle("Console window");
	}

	public final void addCustomCommand(final Command c) {
		this.cl.add(c);
	}

	/**
	 * This should be within Command, but java has a hissy fit because it's not
	 * a top level type.
	 * 
	 * This is a factory method for creating multipul references to a single
	 * command
	 * 
	 * @param firstParts
	 * @return
	 * @see Command
	 */
	public void addCustomCommand(final Command c, final String... otherReferenaces) {
		this.cl.put(c.getFirstPart(), c);

		for (final String part : otherReferenaces) {
			this.cl.put(part, c);
		}
	}

	@Override
	public void keyPressed(final KeyEvent arg0) {
	}

	@Override
	public void keyReleased(final KeyEvent evt) {
		if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
			this.processCommand(this.txtEntry.getText());
			this.txtEntry.setText("");
		} else if ((evt.getKeyCode() == KeyEvent.VK_UP) && this.txtEntry.getText().isEmpty()) {
			System.out.println("Setting last command...");
			this.txtEntry.setText(this.lastCommand);
		}
	}

	@Override
	public void keyTyped(final KeyEvent arg0) {
	}

	public void outputResult(final ResultsBuffer rb) {
		this.outputResult(rb.get());
	}

	void outputResult(final String result) {
		if ((result == null) || result.isEmpty()) {
			return;
		}

		final BufferedReader br = new BufferedReader(new StringReader(result));

		try {
			String line = "";
			while ((line = br.readLine()) != null) {
				this.txtResults.appendOutput("< " + line + "\n");
			}
		} catch (final Exception e) {
			this.txtResults.appendOutput("< (Exception while output result!!)");
			e.printStackTrace();
		}

		// this.txtResults.updateUI(); // appends scrollbars when neccisary.
	}

	/**
	 * An extremely simple command processor, doesn't do anything fancy at
	 * all...
	 * 
	 * ... for the moment :)
	 * 
	 * @param command
	 *            The command to execute.
	 */
	public void processCommand(String command) {
		// check we're not being acosted by spanish sailors, who send us empty
		// commands just for fun.
		if (command.isEmpty()) {
			this.txtResults.appendInput("> \n");
			return;
		}

		// normalize it
		command = command.trim();
		command = command.toLowerCase();

		if (command.startsWith("/")) {
			command = command.replaceFirst("/", "");
		}

		// display it
		this.txtResults.appendInput("> " + command + "\n");
		this.lastCommand = command;

		// find it
		if (!this.cl.containsKey(command)) {
			System.out.println(this.cl.toString());
			this.outputResult("Command not found: " + command);
			return;
		}

		try {
			this.cl.get(command).invoke();
		} catch (final Exception e) {
			this.outputResult("Error trying to invoke the command.");
			LOG.error("processCommand exception:", e);
		}

	}

	private void setupStandardCommands() {
		this.cl.add(new Command("version") {
			@Override
			public void invoke() {
				ConsoleWindow.this.outputResult(Configuration.getS("sysAppName") + " " + Configuration.getVersion());
				ConsoleWindow.this.outputResult("\tis stable: " + Util.isStable());
			}
		});

		this.cl.add(new Command("show commands") {
			@Override
			public void invoke() {
				final TreeSet<String> ts = new TreeSet<String>();
				ts.addAll(ConsoleWindow.this.cl.keySet());

				for (final String key : ts) {
					ConsoleWindow.this.outputResult(key);
				}

				ConsoleWindow.this.outputResult("** End of list");
			}
		});

		this.cl.add(new Command("show cfgtable") {
			@Override
			public void invoke() {
				ConsoleWindow.this.outputResult(Configuration.getTable());
			}
		});

		this.cl.add(new Command("wclose") {
			@Override
			public void invoke() {
				ConsoleWindow.this.setVisible(false);
			}
		});

		this.cl.add(new Command("wmax") {
			@Override
			public void invoke() {
				ConsoleWindow.this.setExtendedState(ConsoleWindow.this.getExtendedState() | Frame.MAXIMIZED_BOTH);
			}
		});

		this.cl.add(new Command("logger trace") {
			@Override
			public void invoke() {
				Configuration.set("logger.traces", !Configuration.getB("logger.traces"));
				ConsoleWindow.this.outputResult("Logger traces are now: " + ((Configuration.getB("logger.traces")) ? "ON" : "OFF"));
			}
		});

		this.cl.add(new Command("help") {
			@Override
			public void invoke() {
				ConsoleWindow.this.outputResult("This function has not been written yet, try \"show commands\".");
			}
		});

		this.addCustomCommand(new Command("show plat") {
			@Override
			public void invoke() {
				ConsoleWindow.this.outputResult(Util.getPlatformInfo());
			}
		});

		this.addCustomCommand(new Command("exit") {
			@Override
			public void invoke() {
				ConsoleWindow.this.outputResult("Bye!");
				System.exit(0);
			}
		}, "quit");

		this.addCustomCommand(new Command("clear") {
			@Override
			public void invoke() {
				ConsoleWindow.this.txtResults.setText("");
			}
		}, "cls");

		this.addCustomCommand(new Command("show debuginfo") {
			@Override
			public void invoke() {
				ConsoleWindow.this.outputResult(Configuration.getS("sysAppName") + " " + Configuration.getVersion());
				ConsoleWindow.this.outputResult("-- System");
				ConsoleWindow.this.outputResult(Util.getSystemInfo());
				ConsoleWindow.this.outputResult("-- Platform");
				ConsoleWindow.this.outputResult(Util.getPlatformInfo());
				ConsoleWindow.this.outputResult("-- cfg table");
				ConsoleWindow.this.outputResult(Configuration.getTable());
			}
		});

	}

	@Override
	public void setVisible(final boolean vis) {
		super.setVisible(vis);

		// FIXME this would make a nice configuration option
		if (vis) {
			this.cl.clear();
			this.setupStandardCommands();

			this.txtEntry.requestFocus();
		} else {
			this.cl.clear();
		}
	}

	public void toggleVisible() {
		if (this.isVisible()) {
			this.setVisible(false);
		} else {
			this.setVisible(true);
		}

	}
}
