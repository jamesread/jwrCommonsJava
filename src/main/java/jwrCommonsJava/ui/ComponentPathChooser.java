package jwrCommonsJava.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class ComponentPathChooser extends JPanel {
	private static final long serialVersionUID = 1L;
	protected final JTextField jta = new JTextField();
	protected File selectedFile;

	private final JButtonWithAl btn = new JButtonWithAl("Browse") {
		private static final long serialVersionUID = 1L;

		@Override
		public void click() {
			ComponentPathChooser.this.showChooser();
		}    
	}; 

	private final JLabel lbl = new JLabel();

	public ComponentPathChooser(String title) {
		this.lbl.setText(title);
		this.jta.setEditable(false);
		this.setupComponents();
	}

	public File getSelectedFile() {
		return this.selectedFile;
	}

	public void onSelectionChanged() {
	}

	private void setupComponents() {
		this.setLayout(new BorderLayout());
		this.add(this.lbl, BorderLayout.WEST);
		this.add(this.jta, BorderLayout.CENTER);  
		this.add(this.btn, BorderLayout.EAST);
	}

	public abstract void showChooser();

	protected boolean validateSelectedPath(File sf) {
		return true;
	}
}
