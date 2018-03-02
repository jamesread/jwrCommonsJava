package jwrCommonsJava;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;

public abstract class GuiComponentModel extends JFrame {
	protected final GridBagConstraints gbc = Util.getNewGbc();
	protected final GridBagLayout gbl = new GridBagLayout();

	public void add(final JComponent c) {
		this.gbl.setConstraints(c, this.gbc);
		super.add(c);
	}

	protected abstract void setupComponents();
}
