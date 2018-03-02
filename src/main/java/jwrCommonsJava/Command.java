package jwrCommonsJava;

public abstract class Command {
	private final String firstPart;

	public Command() {
		this.firstPart = "";
	}

	public Command(final String firstPart) {
		this.firstPart = firstPart;
	}

	public String getFirstPart() {
		return this.firstPart;
	}

	public abstract void invoke();

	/**
	 * This is for custom commands, so they can use the result window.
	 */
	protected void redirectOutputResult(final String s) {
		ConsoleWindow.instance.outputResult(s);
	}
}