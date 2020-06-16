package bg.sofia.uni.fmi.mjt.auth.domain;

import java.nio.channels.SocketChannel;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.Command;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class SystemFacade {
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FAILED_LOGIN_MAX = 3;

	private static DataOrganizer dataOrganizer;
	private static SystemFacade instance;

	private String result = null;

	private SystemFacade(DataOrganizer organizer) {
		dataOrganizer = organizer;
	}

	public static DataOrganizer getDataOrganizer() {
		return dataOrganizer;
	}

	public static SystemFacade getInstance(DataOrganizer organizer) {
		if (instance == null) {
			instance = new SystemFacade(organizer);
		}
		return instance;
	}

	public void loadUsers() {
		dataOrganizer.getUserFileEditor().load(dataOrganizer);
	}


	public String registerInSystem(SocketChannel socketChannel, AuthenticatedUser newUser) {
		return dataOrganizer.registerInSystem(socketChannel, newUser);
	}

	public String logInByNameAndPass(String username, String password, SocketChannel socketChannel) {
		return dataOrganizer.logInByNameAndPass(username, password, socketChannel);
	}

	public String logInBySession(String sessionId, SocketChannel socketChannel) {
		return dataOrganizer.logInBySession(sessionId, socketChannel);
	}

	public String resetPassword(String userSession, String username, String oldPassword, String newPassword,
			SocketChannel socketChannel) {
		return dataOrganizer.resetPassword(userSession, username, oldPassword, newPassword, socketChannel);
	}

	public String updateUser(String[] tokens, String currSessionID, String username, SocketChannel socketChannel) {
		return dataOrganizer.updateUser(tokens, currSessionID, username, socketChannel);
	}

	public String logout(String currSessionID, String currUsername, SocketChannel socketChannel) {
		return dataOrganizer.logout(currSessionID, currUsername, socketChannel);
	}

	public String addAdmin(String currUsername, String usernameToMakeAdmin, String currSessionID,
			SocketChannel socketChannel) {
		return dataOrganizer.addAdmin(currUsername, usernameToMakeAdmin, currSessionID, socketChannel);
	}

	public String removeAdmin(String currUsername, String usernameToRemoveAdmin, String currSessionID,
			SocketChannel socketChannel) {
		return dataOrganizer.removeAdmin(currUsername, usernameToRemoveAdmin, currSessionID, socketChannel);
	}

	public String deleteUser(String currUsername, String usernameToDelete, String currSessionID,
			SocketChannel socketChannel) {
		return dataOrganizer.deleteUser(currUsername, usernameToDelete, currSessionID, socketChannel);
	}

}
