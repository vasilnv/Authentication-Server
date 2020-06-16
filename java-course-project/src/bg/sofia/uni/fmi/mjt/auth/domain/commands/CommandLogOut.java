package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class CommandLogOut implements CommandOperation{
	private static final String STRING_DELIMITER = " ";
	private static final int SESSION_ARGUMENT = 1;

	private String message;
	private Domain domain;
	private SocketChannel socketChannel;

	public CommandLogOut(SocketChannel channel, String message, Domain domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(STRING_DELIMITER);
		String currUsername = domain.getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[SESSION_ARGUMENT];
		return domain.logout(currSessionID, currUsername);
	}
}
