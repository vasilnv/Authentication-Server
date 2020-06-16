package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class CommandRegister implements CommandOperation {
	private static final String STRING_DELIMITER = " ";
	private static final String STRING_ENCODER = "javaIsCool";

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
		String[] tokens = message.split(STRING_DELIMITER);
		int index = 1;
		String username = tokens[index++];
		String password = tokens[index++];
		password = PasswordEncoder.encodePass(password, STRING_ENCODER);
		String firstname = tokens[index++];
		String lastname = tokens[index++];
		String email = tokens[index++];

		AuthenticatedUser newUser = new AuthenticatedUser(username, password, firstname, lastname, email);
		return domain.registerInSystem(socketChannel, newUser);
	}
}
