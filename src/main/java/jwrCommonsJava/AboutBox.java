package jwrCommonsJava;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutBox extends JDialog {
	private class AvatarComponent extends JComponent {
		private final BufferedImage bi;

		public AvatarComponent(final BufferedImage bi) {
			this.bi = bi;
			this.setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
		}

		@Override
		public void paint(final Graphics g) {
			g.drawImage(this.bi, 0, 0, null);
		}
	}

	private class JSeparator extends JComponent {
		public JSeparator() {
			this.setMinimumSize(new Dimension(10, 1));
		}

		@Override
		public void paint(final Graphics g) {
			g.drawLine(0, 0, this.getWidth(), 0);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(AboutBox.class);

	private final GridBagConstraints gbc = Util.getNewGbc();
	private final GridBagLayout gbl = new GridBagLayout();

	public AboutBox() {
		this.setSize(380, 180);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setTitle(Configuration.getS("sysAppName") + " - About");

		this.setupComponents();
	}

	private void setupComponents() {
		this.setLayout(this.gbl);

		this.gbc.anchor = GridBagConstraints.NORTHWEST;

		try {
			final InputStream is = JarUtil.getResource("resources/images/avatar.jpg");

			this.gbc.gridheight = GridBagConstraints.REMAINDER;
			this.add(new AvatarComponent(ImageIO.read(is)), this.gbc);
			this.gbc.gridy = 0;
			this.gbc.gridx++;
		} catch (final Exception e) {
			e.printStackTrace();
			LOG.warn("Could not render avatar for about box. ");
		}

		this.gbc.gridheight = 1;
		this.gbc.weightx = 1;
		this.gbc.weighty = 0;
		this.gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel("Application: " + Configuration.getS("sysAppName")), this.gbc);
		this.gbc.gridy++;
		this.add(new JLabel("Version: " + Configuration.getVersion()), this.gbc);
		this.gbc.gridy++;
		this.add(new JSeparator(), this.gbc);
		this.gbc.gridy++;
		this.add(new JLabel("Written by: James Read"), this.gbc);
		this.gbc.gridy++;
		this.add(new JLabel("Platform: " + Util.getPlatformInfo()), this.gbc);
	}
}
