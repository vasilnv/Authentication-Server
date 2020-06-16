package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class CommandDistributor {
	private static final int ZERO_ARG = 0;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FIFTH_ARG = 5;
	private static final int SIXTH_ARG = 6;

	public void messageReceive(String messageToServer, SocketChannel socketChannel, Domain domain) {
		String[] tokens = messageToServer.split(" ");
		String command = tokens[ZERO_ARG];
		OutputHandler output = new OutputHandler(socketChannel);
		String resultMessage = null;
		CommandExecutor executor = new CommandExecutor();
		if (command.equals(Command.REGISTER.getCommand()) && tokens.length == SIXTH_ARG) {
			CommandOperation operation = new CommandRegister(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else if (command.equals(Command.LOGIN.getCommand())
				&& (tokens.length == SECOND_ARG || tokens.length == THIRD_ARG)) {
			CommandOperation operation = new CommandLogIn(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else if (command.equals(Command.RESET_PASSWORD.getCommand()) && tokens.length == FIFTH_ARG) {
			CommandOperation operation = new CommandResetPassword(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else if (command.equals(Command.UPDATE_USER.getCommand())) {
			CommandOperation operation = new CommandUpdateUser(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else if (command.equals(Command.LOGOUT.getCommand()) && tokens.length == SECOND_ARG) {
			CommandOperation operation = new CommandLogOut(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else if (command.equals(Command.ADD_ADMIN.getCommand()) && tokens.length == THIRD_ARG) {
			CommandOperation operation = new CommandAddAdmin(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else if (command.equals(Command.REMOVE_ADMIN.getCommand()) && tokens.length == THIRD_ARG) {
			CommandOperation operation = new CommandRemoveAdmin(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else if (command.equals(Command.DELETE_USER.getCommand()) && tokens.length == THIRD_ARG) {
			CommandOperation operation = new CommandDeleteUser(socketChannel, messageToServer, domain);
			resultMessage = executor.executeOperation(operation);
		} else {
			resultMessage = "wrong command";
		}
		output.write(resultMessage);

	}

}
