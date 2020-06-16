package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class CommandUpdateUser implements CommandOperation{
	private static final String STRING_DELIMITER = " ";
	private static final int FIRST_ARG = 1;
	private static final int ITERATION_STEP_TWO = 2;
	private static final int THIRD_ARG = 3;

	private String message;
	private Domain domain;
	private SocketChannel socketChannel;

	public CommandUpdateUser(SocketChannel channel, String message, Domain domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(STRING_DELIMITER);
		String username = domain.getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[FIRST_ARG];
		String result = "wrong command";
		for (int i = THIRD_ARG; i < tokens.length; i += ITERATION_STEP_TWO) {
			if (tokens[i - 1].equals(CommandParameter.NEW_USERNAME.getCommand())) {
				result = domain.updateUser(currSessionID, username, CommandParameter.NEW_USERNAME.getCommand(), tokens[i]);
			}
			if (tokens[i - 1].equals(CommandParameter.NEW_FIRSTNAME.getCommand())) {
				result = domain.updateUser(currSessionID, username, CommandParameter.NEW_FIRSTNAME.getCommand(), tokens[i]);
			}
			if (tokens[i - 1].equals(CommandParameter.NEW_LASTNAME.getCommand())) {
				result = domain.updateUser(currSessionID, username, CommandParameter.NEW_LASTNAME.getCommand(), tokens[i]);
			}
			if (tokens[i - 1].equals(CommandParameter.NEW_EMAIL.getCommand())) {
				result = domain.updateUser(currSessionID, username, CommandParameter.NEW_EMAIL.getCommand(), tokens[i]);
			}
		}
		return result;
	}

}
