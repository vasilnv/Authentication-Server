package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;

public class CommandResetPassword implements CommandOperation{
	private static final String STRING_DELIMITER = " ";
	private static final String STRING_ENCODER = "javaIsCool";
	private static final int SESSION_ID_ARGUMENT = 1;
	private static final int USERNAME_ARGUMENT = 2;
	private static final int OLD_PASSWORD_ARGUMENT = 3;
	private static final int NEW_PASSWORD_ARGUMENT = 4;

	private String message;
	private Domain domain;
	private SocketChannel socketChannel;

	public CommandResetPassword(SocketChannel channel, String message, Domain domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(STRING_DELIMITER);
		String userSession = tokens[SESSION_ID_ARGUMENT];
		String username = tokens[USERNAME_ARGUMENT];
		String oldPassword = tokens[OLD_PASSWORD_ARGUMENT];
		String newPassword = tokens[NEW_PASSWORD_ARGUMENT];
		oldPassword = PasswordEncoder.encodePass(oldPassword, STRING_ENCODER);
		newPassword = PasswordEncoder.encodePass(newPassword, STRING_ENCODER);
		String result = domain.resetPassword(userSession, username, oldPassword, newPassword, socketChannel);
		return result;
	}
}


