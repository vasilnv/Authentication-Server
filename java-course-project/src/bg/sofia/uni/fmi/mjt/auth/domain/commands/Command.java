package bg.sofia.uni.fmi.mjt.auth.domain.commands;

public enum Command {
	REGISTER("register"), 
	LOGIN("login"), 
	UPDATE_USER("update-user"), 
	RESET_PASSWORD("reset-password"),
	LOGOUT("logout"),
	ADD_ADMIN("add-admin-user"),
	REMOVE_ADMIN("remove-admin-user"),
	DELETE_USER("delete-user");

	private String command;

	private Command(String command) {
		this.command = command;
	}

	public String getCommand() {
		return this.command;
	}
}
