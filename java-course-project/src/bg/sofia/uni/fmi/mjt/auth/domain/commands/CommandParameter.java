package bg.sofia.uni.fmi.mjt.auth.domain.commands;

public enum CommandParameter {
	NEW_USERNAME("new-username"), 
	NEW_FIRSTNAME("new-firstname"),
	NEW_LASTNAME("new-lastname"), 
	NEW_EMAIL("new-email");

	private String command;

	private CommandParameter(String command) {
		this.command = command;
	}

	public String getCommand() {
		return this.command;
	}
}
