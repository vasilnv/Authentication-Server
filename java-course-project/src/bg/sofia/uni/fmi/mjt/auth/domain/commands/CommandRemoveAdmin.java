package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class CommandRemoveAdmin implements CommandOperation{
	private static final int SESSION_ID_ARGUMENT = 1;
	private static final int USERNAME_ARGUMENT = 2;

	private String message;
	private Domain domain;
	private SocketChannel socketChannel;

	public CommandRemoveAdmin(SocketChannel channel, String message, Domain domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(" ");
		String currUsername = domain.getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[SESSION_ID_ARGUMENT];
		String usernameToRemoveAdmin = tokens[USERNAME_ARGUMENT];
		String result = domain.removeAdmin(currUsername, usernameToRemoveAdmin, currSessionID, socketChannel);
		return result;
	}

}
