package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class CommandRegister implements CommandOperation {
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FOURTH_ARG = 4;
	private static final int FIFTH_ARG = 5;

	private String message;
	private Domain domain;
	private SocketChannel socketChannel;

	public CommandRegister(SocketChannel channel, String message, Domain domain) {
		this.message = message;
		this.domain = domain;
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
		return domain.registerInSystem(socketChannel, newUser);
	}
}
