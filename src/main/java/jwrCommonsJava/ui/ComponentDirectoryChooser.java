package jwrCommonsJava.ui;

import java.io.File;

import javax.swing.JFileChooser;

public class ComponentDirectoryChooser extends ComponentPathChooser {

	private final JFileChooser jfc = new JFileChooser();

	private final String title;

	public ComponentDirectoryChooser(String title) {
		super(title);
		this.title = title;
		this.jta.setText(this.getInitialValue());
		this.selectedFile = new File(this.getInitialValue());
	}

	protected String getInitialValue() {
		return "";
	}

	@Override
	public void showChooser() {
		this.jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.jfc.setDialogTitle(this.title);
		this.jfc.setBounds(100, 100, 320, 240);
		this.jfc.setCurrentDirectory(new File(this.getInitialValue()));
		this.jfc.showOpenDialog(this);

		if (this.jfc.getSelectedFile() != null) {
			while (!this.validateSelectedPath(this.jfc.getSelectedFile())) {
				this.showChooser();
			}

			this.selectedFile = this.jfc.getSelectedFile();
			this.jta.setText(this.selectedFile.getAbsolutePath());

			this.onSelectionChanged();
		}
	}

	@Override
	protected boolean validateSelectedPath(File sf) {
		if (!sf.isDirectory()) {
			return false;
		}

		return true;
	}
}
