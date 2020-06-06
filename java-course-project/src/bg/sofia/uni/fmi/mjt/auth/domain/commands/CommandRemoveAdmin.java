package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class CommandRemoveAdmin implements CommandOperation{
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;

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
		String currUsername = Domain.getDataOrganizer().getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[FIRST_ARG];
		String usernameToRemoveAdmin = tokens[SECOND_ARG];
		String result = domain.removeAdmin(currUsername, usernameToRemoveAdmin, currSessionID, socketChannel);
		return result;
	}

}
