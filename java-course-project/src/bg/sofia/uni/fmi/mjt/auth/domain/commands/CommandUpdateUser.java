package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class CommandUpdateUser implements CommandOperation{
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
		String[] tokens = message.split(" ");
		String username = domain.getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[FIRST_ARG];
		String result = "wrong command";
		for (int i = THIRD_ARG; i < tokens.length; i += ITERATION_STEP_TWO) {
			if (tokens[i - 1].equals("new-username")) {
				result = domain.updateUser(currSessionID, username, "new-username", tokens[i]);
			}
			if (tokens[i - 1].equals("new-firstname")) {
				result = domain.updateUser(currSessionID, username, "new-firstname", tokens[i]);
			}
			if (tokens[i - 1].equals("new-lastname")) {
				result = domain.updateUser(currSessionID, username, "new-lastname", tokens[i]);
			}
			if (tokens[i - 1].equals("new-email")) {
				result = domain.updateUser(currSessionID, username, "new-email", tokens[i]);
			}
		}
		return result;
	}

}
