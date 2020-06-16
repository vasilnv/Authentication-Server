package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;

public class CommandUpdateUser implements CommandOperation{
	private static final int FIRST_ARG = 1;

	private String message;
	private SystemFacade domain;
	private SocketChannel socketChannel;

	public CommandUpdateUser(SocketChannel channel, String message, SystemFacade domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(" ");
		String username = SystemFacade.getDataOrganizer().getChannelsByUsername().get(socketChannel);
		String currSessionID = tokens[FIRST_ARG];
		String result = domain.updateUser(tokens, currSessionID, username);
		return result;
	}

}
