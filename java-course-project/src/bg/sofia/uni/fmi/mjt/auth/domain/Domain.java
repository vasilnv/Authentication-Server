package bg.sofia.uni.fmi.mjt.auth.domain;

import java.nio.channels.SocketChannel;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.Command;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class Domain {
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FAILED_LOGIN_MAX = 3;

	private static DataOrganizer dataOrganizer;
	private static Domain instance;

	private String result = null;

	private Domain(DataOrganizer organizer) {
		dataOrganizer = organizer;
	}

	public static DataOrganizer getDataOrganizer() {
		return dataOrganizer;
	}

	public static Domain getInstance(DataOrganizer organizer) {
		if (instance == null) {
			instance = new Domain(organizer);
		}
		return instance;
	}

	public void loadUsers() {
		dataOrganizer.getUserFileEditor().load(dataOrganizer);
	}

	public String registerInSystem(SocketChannel socketChannel, AuthenticatedUser newUser) {
		String username = newUser.getUsername();
		if (!dataOrganizer.checkIfUserExists(username)) {
			newUser.register(dataOrganizer);
			dataOrganizer.getUserFileEditor().writeUserInFile(newUser);
			dataOrganizer.getChannelsByUsername().put(socketChannel, username);
			dataOrganizer.getUsernamesByChannel().put(username, socketChannel);
			dataOrganizer.getUsers().put(username, newUser);
			if (dataOrganizer.getAdmins().isEmpty()) {
				dataOrganizer.getAdmins().add(username);
			}
			result = "completed registration with sessionId: " + dataOrganizer.getUsersSessions().get(username);
		} else {
			result = "unsuccessful registration";
		}
		return result;
	}

	public String logInByNameAndPass(String username, String password, SocketChannel socketChannel) {
		if (dataOrganizer.checkIfUserExists(username) && dataOrganizer.checkIfUserIsBlocked(username)) {
			result = "You are blocked";
		} else if (dataOrganizer.checkIfUserExists(username)
				&& dataOrganizer.getUsers().get(username).getPassword().equals(password)) {
			Session session = new Session();
			dataOrganizer.addSession(session);
			dataOrganizer.getUsersSessions().put(username, session.getId());
			dataOrganizer.getSessionsUsers().put(session.getId(), username);
			dataOrganizer.getChannelsByUsername().put(socketChannel, username);
			dataOrganizer.getUsernamesByChannel().put(username, socketChannel);

			result = "successful login with sessionID: " + session.getId();

		} else if (dataOrganizer.getUsers().containsKey(username)) {
			dataOrganizer.getUsers().get(username).incrementloginFailed();
			if (dataOrganizer.getUsers().get(username).getLoginFailed() == FAILED_LOGIN_MAX) {
				dataOrganizer.getUsers().get(username).block();
				dataOrganizer.getLogger().writeFailedLogin(socketChannel, username);
				result = "You are blocked please try again in 50 seconds";
				return result;
			}
			result = "unsuccessful login";
		} else {
			dataOrganizer.getLogger().writeFailedLogin(socketChannel, username);
			result = "unsuccessful login";
		}
		return result;
	}

	public String logInBySession(String sessionId, SocketChannel socketChannel) {
		if (dataOrganizer.getSessions().containsKey(sessionId)) {
			String username = dataOrganizer.getSessionsUsers().get(sessionId);
			dataOrganizer.getChannelsByUsername().put(socketChannel, username);
			dataOrganizer.getUsernamesByChannel().put(username, socketChannel);
			dataOrganizer.getSessionsUsers().put(sessionId, username);
			dataOrganizer.getUsersSessions().put(username, sessionId);

			result = "successful login with sessionID: " + sessionId;
		} else {
			result = "unsuccessful login";
		}
		return result;
	}

	public String resetPassword(String userSession, String username, String oldPassword, String newPassword,
			SocketChannel socketChannel) {
		if (dataOrganizer.checkIfUserExists(username) && !dataOrganizer.isUserLoggedIn(userSession)) {
			result = "not logged in";
		} else if (dataOrganizer.checkIfUserExists(username)
				&& dataOrganizer.getChannelsByUsername().get(socketChannel).equals(username)
				&& dataOrganizer.getUsers().get(username).getPassword().equals(oldPassword)) {
			dataOrganizer.changePassword(username, newPassword);
			result = "succesfully changed password";
		} else {
			result = "unsuccessfully changed password";
		}
		return result;
	}

	public String updateUser(String[] tokens, String currSessionID, String username, SocketChannel socketChannel) {
		if (!dataOrganizer.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (dataOrganizer.getSessions().containsKey(currSessionID)
				&& dataOrganizer.getUsersSessions().get(username).equals(currSessionID)) {
			for (int i = THIRD_ARG; i < tokens.length; i += SECOND_ARG) {
				if (tokens[i - 1].equals("new-username")) {
					dataOrganizer.changeUsername(username, tokens[i]);
				}
				if (tokens[i - 1].equals("new-firstname")) {
					dataOrganizer.changeFirstName(username, tokens[i]);
				}
				if (tokens[i - 1].equals("new-lastname")) {
					dataOrganizer.changeLastName(username, tokens[i]);
				}
				if (tokens[i - 1].equals("new-email")) {
					dataOrganizer.changeEmail(username, tokens[i]);
				}
			}
			result = "successful update";
		} else {
			result = "unsuccesful update";
		}
		return result;
	}

	public String logout(String currSessionID, String currUsername, SocketChannel socketChannel) {
		if (!dataOrganizer.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (currUsername.equals(dataOrganizer.getSessionsUsers().get(currSessionID))) {
			dataOrganizer.getSessions().remove(currSessionID);
			dataOrganizer.getSessionsUsers().remove(currSessionID);
			result = "login again";
		} else {
			result = "unsuccessful logout";
		}
		return result;
	}

	public String addAdmin(String currUsername, String usernameToMakeAdmin, String currSessionID,
			SocketChannel socketChannel) {
		dataOrganizer.getLogger().writeConfigChageStart(socketChannel, currUsername, usernameToMakeAdmin,
				Command.ADD_ADMIN.getCommand());
		if (!dataOrganizer.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (dataOrganizer.getAdmins().contains(currUsername)
				&& dataOrganizer.getSessionsUsers().get(currSessionID).equals(currUsername)) {
			dataOrganizer.getAdmins().add(usernameToMakeAdmin);
			dataOrganizer.getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToMakeAdmin, true);
			result = "successfully made admin";
		} else {
			dataOrganizer.getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToMakeAdmin, false);
			result = "unsuccessfully made admin";
		}
		return result;
	}

	public String removeAdmin(String currUsername, String usernameToRemoveAdmin, String currSessionID,
			SocketChannel socketChannel) {
		OutputHandler output = new OutputHandler(socketChannel);
		dataOrganizer.getLogger().writeConfigChageStart(socketChannel, currUsername, usernameToRemoveAdmin,
				Command.REMOVE_ADMIN.getCommand());
		if (!dataOrganizer.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (dataOrganizer.getAdmins().contains(currUsername)
				&& dataOrganizer.getSessionsUsers().get(currSessionID).equals(currUsername)) {
			dataOrganizer.getAdmins().remove(usernameToRemoveAdmin);
			dataOrganizer.getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToRemoveAdmin, true);
			result = "successfully removed admin";
		} else {
			dataOrganizer.getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToRemoveAdmin, false);
			result = "unsuccessfully removed admin";
		}
		return result;
	}

	public String deleteUser(String currUsername, String usernameToDelete, String currSessionID,
			SocketChannel socketChannel) {
		dataOrganizer.getLogger().writeConfigChageStart(socketChannel, currUsername, usernameToDelete,
				Command.DELETE_USER.getCommand());
		if (!dataOrganizer.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (dataOrganizer.getAdmins().contains(currUsername)
				&& dataOrganizer.getSessionsUsers().get(currSessionID).equals(currUsername)) {
			dataOrganizer.getUsers().remove(usernameToDelete);
			String userToDeleteSession = dataOrganizer.getUsersSessions().get(usernameToDelete);
			dataOrganizer.getUsersSessions().remove(usernameToDelete);
			if (userToDeleteSession != null) {
				dataOrganizer.getSessionsUsers().remove(userToDeleteSession);
				dataOrganizer.getSessions().remove(userToDeleteSession);
			}
			dataOrganizer.getAdmins().remove(usernameToDelete);
			dataOrganizer.getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToDelete, true);
			dataOrganizer.getUserFileEditor().deleteUserFromFile(usernameToDelete);
			result = "succesfully deleted user";
		} else {
			dataOrganizer.getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToDelete, false);
			result = "unsuccessfully deleted user";
		}
		return result;
	}
}
