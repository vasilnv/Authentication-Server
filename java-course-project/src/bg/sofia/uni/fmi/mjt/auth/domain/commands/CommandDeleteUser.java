package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;

public class CommandDeleteUser implements CommandOperation{
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;

	private String message;
	private SystemFacade domain;
	private SocketChannel socketChannel;

	public CommandDeleteUser(SocketChannel channel, String message, SystemFacade domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(" ");
		String currUsername = SystemFacade.getDataOrganizer().getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[FIRST_ARG];
		String usernameToDeleteUser = tokens[SECOND_ARG];
		return domain.deleteUser(currUsername, usernameToDeleteUser, currSessionID, socketChannel);
	}

}
