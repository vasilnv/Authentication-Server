package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class CommandAddAdmin implements CommandOperation{
	private static final int NULL_ARG = 0;
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FOURTH_ARG = 4;
	private static final int FIFTH_ARG = 5;
	private static final int TIME_TO_LOCK = 50;
	private static final int FAILED_LOGIN_MAX = 3;

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
		String[] tokens = message.split(" ");
		String currUsername = Domain.getDataOrganizer().getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[FIRST_ARG];
		String usernameToMakeAdmin = tokens[SECOND_ARG];
		String message = domain.addAdmin(currUsername, usernameToMakeAdmin, currSessionID, socketChannel);
		return message;
	}
}
