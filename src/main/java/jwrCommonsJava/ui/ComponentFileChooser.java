package jwrCommonsJava.ui;

import javax.swing.JOptionPane;

public class ComponentFileChooser extends ComponentPathChooser {

	public ComponentFileChooser(String title) {
		super(title);
	}

	@Override
	public void showChooser() {
		JOptionPane.showMessageDialog(this, "File choosing is not yet supported.");
	}

}
