package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;

public class CommandLogIn implements CommandOperation {
	private static final String STRING_DELIMITER = " ";
	private static final String STRING_ENCODER = "javaIsCool";
	private static final int USERNAME_ARGUMENT = 1;
	private static final int PASSWORD_ARGUMENT = 2;
	private static final int THREE_NUMBER_OF_TOKENS = 3;
	private static final int TWO_NUMBER_OF_TOKENS = 2;

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
		String[] tokens = message.split(STRING_DELIMITER);

		if (tokens.length == THREE_NUMBER_OF_TOKENS) {
			String username = tokens[USERNAME_ARGUMENT];
			String password = tokens[PASSWORD_ARGUMENT];
			password = PasswordEncoder.encodePass(password, STRING_ENCODER);
			return domain.logInByNameAndPassword(username, password, socketChannel);
		} else if (tokens.length == TWO_NUMBER_OF_TOKENS) {
			String sessionId = tokens[USERNAME_ARGUMENT];
			return domain.logInBySession(sessionId, socketChannel);

		}
		return "wrong command";
	}
}
