package bg.sofia.uni.fmi.mjt.auth.domain;

import java.nio.channels.SocketChannel;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class SystemFacade {
	private static Domain domain;
	private static SystemFacade instance;


	private SystemFacade(Domain organizer) {
		domain = organizer;
	}

	public static Domain getDataOrganizer() {
		return domain;
	}

	public static SystemFacade getInstance(Domain organizer) {
		if (instance == null) {
			instance = new SystemFacade(organizer);
		}
		return instance;
	}

	public void loadUsers() {
		domain.loadUsers();
	}

	public String registerInSystem(SocketChannel socketChannel, AuthenticatedUser newUser) {
		return domain.registerInSystem(socketChannel, newUser);
	}

	public String logInByNameAndPass(String username, String password, SocketChannel socketChannel) {
		return domain.logInByNameAndPass(username, password, socketChannel);
	}

	public String logInBySession(String sessionId, SocketChannel socketChannel) {
		return domain.logInBySession(sessionId, socketChannel);
	}

	public String resetPassword(String userSession, String username, String oldPassword, String newPassword,
			SocketChannel socketChannel) {
		return domain.resetPassword(userSession, username, oldPassword, newPassword, socketChannel);
	}

	public String updateUser(String currSessionID, String username, String command, String updatedData) {
		return domain.updateUser(currSessionID, username, command, updatedData);
	}

	public String logout(String currSessionID, String currUsername, SocketChannel socketChannel) {
		return domain.logout(currSessionID, currUsername, socketChannel);
	}

	public String addAdmin(String currUsername, String usernameToMakeAdmin, String currSessionID,
			SocketChannel socketChannel) {
		return domain.addAdmin(currUsername, usernameToMakeAdmin, currSessionID, socketChannel);
	}

	public String removeAdmin(String currUsername, String usernameToRemoveAdmin, String currSessionID,
			SocketChannel socketChannel) {
		return domain.removeAdmin(currUsername, usernameToRemoveAdmin, currSessionID, socketChannel);
	}

	public String deleteUser(String currUsername, String usernameToDelete, String currSessionID,
			SocketChannel socketChannel) {
		return domain.deleteUser(currUsername, usernameToDelete, currSessionID, socketChannel);
	}

}
