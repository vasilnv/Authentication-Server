package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;

public class CommandLogIn implements CommandOperation {
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;

	private String message;
	private Domain domain;
	SocketChannel socketChannel;

	public CommandLogIn(SocketChannel channel, String message, Domain domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(" ");

		if (tokens.length == THIRD_ARG) {
			String username = tokens[FIRST_ARG];
			String password = tokens[SECOND_ARG];
			password = PasswordEncoder.encodePass(password, "javaIsCool");
			return domain.logInByNameAndPass(username, password, socketChannel);
		} else if (tokens.length == SECOND_ARG) {
			String sessionId = tokens[FIRST_ARG];
			return domain.logInBySession(sessionId, socketChannel);

		}
		return "wrong command";
	}
}
