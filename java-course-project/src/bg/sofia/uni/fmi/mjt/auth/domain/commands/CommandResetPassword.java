package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class CommandResetPassword implements CommandOperation{
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FOURTH_ARG = 4;

	private String message;
	private SystemFacade domain;
	private SocketChannel socketChannel;

	public CommandResetPassword(SocketChannel channel, String message, SystemFacade domain) {
		this.message = message;
		this.domain = domain;
		this.socketChannel = channel;
	}

	@Override
	public String execute() {
		String[] tokens = message.split(" ");
		String userSession = tokens[FIRST_ARG];
		String username = tokens[SECOND_ARG];
		String oldPassword = tokens[THIRD_ARG];
		String newPassword = tokens[FOURTH_ARG];
		oldPassword = PasswordEncoder.encodePass(oldPassword, "javaIsCool");
		newPassword = PasswordEncoder.encodePass(newPassword, "javaIsCool");
		String result = domain.resetPassword(userSession, username, oldPassword, newPassword, socketChannel);
		return result;
	}
}


