package jwrCommonsJava.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public abstract class JButtonWithAl extends JButton implements ActionListener {
	public JButtonWithAl(final String title) {
		this(title, true);
	}

	public JButtonWithAl(final String title, boolean enabled) {
		super(title);
		this.addActionListener(this);
		this.setEnabled(enabled);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.click();
	}

	public abstract void click();
}
