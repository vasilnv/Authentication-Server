package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class CommandRegister implements CommandOperation {
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FOURTH_ARG = 4;
	private static final int FIFTH_ARG = 5;

	private String message;
	private SystemFacade systemFacade;
	private SocketChannel socketChannel;

	public CommandRegister(SocketChannel channel, String message, SystemFacade facade) {
		this.message = message;
		this.systemFacade = facade;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(" ");

		String username = tokens[FIRST_ARG];
		String password = tokens[SECOND_ARG];
		password = PasswordEncoder.encodePass(password, "javaIsCool");
		String firstname = tokens[THIRD_ARG];
		String lastname = tokens[FOURTH_ARG];
		String email = tokens[FIFTH_ARG];

		AuthenticatedUser newUser = new AuthenticatedUser(username, password, firstname, lastname, email);
		return systemFacade.registerInSystem(socketChannel, newUser);
	}
}
