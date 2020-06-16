package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class CommandAddAdmin implements CommandOperation{
	private static final String STRING_DELIMITER = " ";
	private static final int SESSION_ID_ARGUMENT = 1;
	private static final int USERNAME_ARGUMENT = 2;

	private String message;
	private Domain domain;
	private SocketChannel socketChannel;

	public CommandAddAdmin(SocketChannel channel, String message, Domain domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(STRING_DELIMITER);
		String currUsername = domain.getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[SESSION_ID_ARGUMENT];
		String usernameToMakeAdmin = tokens[USERNAME_ARGUMENT];
		String message = domain.addAdmin(currUsername, usernameToMakeAdmin, currSessionID, socketChannel);
		return message;
	}
}
