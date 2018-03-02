package jwrCommonsJava.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class JCheckboxWithAl extends JCheckBox implements ActionListener {

	public JCheckboxWithAl(final String title) {
		super(title);
		this.addActionListener(this);
	}

	public void actionPerformed(final ActionEvent e) {
		this.onClicked();
	}

	protected void onClicked() {
	}
}
