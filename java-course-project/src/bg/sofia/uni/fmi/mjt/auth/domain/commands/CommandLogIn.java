package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class CommandLogIn implements CommandOperation {
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;

	private String message;
	private SystemFacade domain;
	SocketChannel socketChannel;

	public CommandLogIn(SocketChannel channel, String message, SystemFacade domain) {
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
