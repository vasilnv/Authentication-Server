package bg.sofia.uni.fmi.mjt.auth.domain;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import bg.sofia.uni.fmi.mjt.auth.FileEditors.AuditLog;
import bg.sofia.uni.fmi.mjt.auth.FileEditors.UserFileEditor;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.Command;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class DataOrganizer {
	private static final int ZERO_ARG = 0;
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FOURTH_ARG = 4;
	private static final int TIME_TO_LOCK = 50;

	private UserFileEditor userFileEditor = new UserFileEditor();
	private AuditLog logger = new AuditLog();

	private Map<SocketChannel, String> channelsByUsername = new HashMap<>();
	private Map<String, SocketChannel> usernamesByChannel = new HashMap<>();

	private Map<String, Session> sessions = new ConcurrentHashMap<>(); // sessionID - session

	private Set<String> admins = new HashSet<>();

	private Map<String, AuthenticatedUser> users = new ConcurrentHashMap<>();

	public DataOrganizer() {
	}
	public Map<String, Session> getSessions() {
		return sessions;
	}

	public Map<SocketChannel, String> getChannelsByUsername() {
		return channelsByUsername;
	}

	public UserFileEditor getUserFileEditor() {
		return userFileEditor;
	}
	public AuditLog getLogger() {
		return logger;
	}
	public Map<String, SocketChannel> getUsernamesByChannel() {
		return usernamesByChannel;
	}

	public Map<String, AuthenticatedUser> getUsers() {
		return users;
	}

	public Set<String> getAdmins() {
		return admins;
	}

	
	public boolean checkIfUserExists(String name) {
		return users.containsKey(name);
	}

	public boolean checkIfUserIsBlocked(String username) {
		if (!users.get(username).getIsBlocked()) {
			return false;
		} else {
			if (users.get(username).getBlockingTime().plusSeconds(TIME_TO_LOCK).isAfter(LocalDateTime.now())) {
				return true;
			} else {
				users.get(username).unblock();
				return false;
			}
		}
	}


	public void addSession(Session session) {
		sessions.put(session.getId(), session);
	}

	public void removeSession(Session session) {
		sessions.remove(session.getId());
		String usernameSession = session.getUsername();
		session.setUsername(null);
		users.get(usernameSession).setSession(null);
		SocketChannel channel = usernamesByChannel.get(usernameSession);
		OutputHandler output = new OutputHandler(channel);
		output.write("Your session expired. Please login again.");
	}

	public void addUserSession(String username, Session session) {
		users.get(username).setSession(session.getId());
	}

	public void addSessionUser(String username, Session session) {
		session.setUsername(username);
	}

	public String changeUsername(String oldUsername, String newUsername) {
		users.get(oldUsername).setUsername(newUsername);
		AuthenticatedUser tmpUser = users.get(oldUsername);
		users.remove(oldUsername);
		users.put(newUsername, tmpUser);
		String oldSession = users.get(oldUsername).getSessionID();
		sessions.get(oldSession).setUsername(newUsername);
		userFileEditor.changeConfiguration(oldUsername, newUsername, ZERO_ARG);
		return newUsername;

	}

	public String changeFirstName(String username, String firstName) {
		users.get(username).setFirstName(firstName);
		userFileEditor.changeConfiguration(username, firstName, SECOND_ARG);
		return firstName;
	}

	public String changeLastName(String username, String lastName) {
		users.get(username).setLastname(lastName);
		userFileEditor.changeConfiguration(username, lastName, THIRD_ARG);
		return lastName;
	}

	public String changeEmail(String username, String email) {
		users.get(username).setEmail(email);
		userFileEditor.changeConfiguration(username, email, FOURTH_ARG);
		return email;
	}

	public boolean isUserLoggedIn(String sessionId) {
		return (sessions.containsKey(sessionId));
	}
	
	public String changePassword(String username, String password) {
		users.get(username).setPassword(password);
		userFileEditor.changeConfiguration(username, password, FIRST_ARG);
		return password;
	}


	
	
	private String result;
	
	public String registerInSystem(SocketChannel socketChannel, AuthenticatedUser newUser) {
		String username = newUser.getUsername();
		if (!checkIfUserExists(username)) {
			Session session = new Session();
			addSession(session);
			session.setUsername(username);
			newUser.setSession(session.getId());
			System.out.println("user registered with session ID :" + session.getId());

			this.getUserFileEditor().writeUserInFile(newUser);
			this.getChannelsByUsername().put(socketChannel, username);
			this.getUsernamesByChannel().put(username, socketChannel);
			this.getUsers().put(username, newUser);
			if (this.getAdmins().isEmpty()) {
				this.getAdmins().add(username);
			}
			result = "completed registration with sessionId: " + session.getId();
		} else {
			result = "unsuccessful registration";
		}
		return result;
	}

	public String logInByNameAndPass(String username, String password, SocketChannel socketChannel) {
		if (this.checkIfUserExists(username) && this.checkIfUserIsBlocked(username)) {
			result = "You are blocked";
		} else if (this.checkIfUserExists(username)
				&& this.getUsers().get(username).getPassword().equals(password)) {
			Session session = new Session();
			this.addSession(session);
			this.getUsers().get(username).setSession(session.getId());
			this.getSessions().get(session.getId()).setUsername(username);
			this.getChannelsByUsername().put(socketChannel, username);
			this.getUsernamesByChannel().put(username, socketChannel);

			result = "successful login with sessionID: " + session.getId();

		} else if (this.getUsers().containsKey(username)) {
			this.getUsers().get(username).incrementloginFailed();
			if (this.getUsers().get(username).getLoginFailed() == 3) {
				this.getUsers().get(username).block();
				this.getLogger().writeFailedLogin(socketChannel, username);
				result = "You are blocked please try again in 50 seconds";
				return result;
			}
			result = "unsuccessful login";
		} else {
			this.getLogger().writeFailedLogin(socketChannel, username);
			result = "unsuccessful login";
		}
		return result;
	}

	public String logInBySession(String sessionId, SocketChannel socketChannel) {
		if (this.getSessions().containsKey(sessionId)) {
			String username = this.getSessions().get(sessionId).getUsername();
			this.getChannelsByUsername().put(socketChannel, username);
			this.getUsernamesByChannel().put(username, socketChannel);
			this.getUsers().get(username).setSession(sessionId);
			this.getSessions().get(sessionId).setUsername(username);

			result = "successful login with sessionID: " + sessionId;
		} else {
			result = "unsuccessful login";
		}
		return result;
	}

	public String resetPassword(String userSession, String username, String oldPassword, String newPassword,
			SocketChannel socketChannel) {
		if (this.checkIfUserExists(username) && !isUserLoggedIn(userSession)) {
			result = "not logged in";
		} else if (this.checkIfUserExists(username)
				&& this.getChannelsByUsername().get(socketChannel).equals(username)
				&& this.getUsers().get(username).getPassword().equals(oldPassword)) {
			this.changePassword(username, newPassword);
			result = "succesfully changed password";
		} else {
			result = "unsuccessfully changed password";
		}
		return result;
	}

	public String updateUser(String[] tokens, String currSessionID, String username, SocketChannel socketChannel) {
		if (!isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (getSessions().containsKey(currSessionID)
				&& getUsers().get(username).getSessionID().equals(currSessionID)) {
			for (int i = THIRD_ARG; i < tokens.length; i += SECOND_ARG) {
				if (tokens[i - 1].equals("new-username")) {
					changeUsername(username, tokens[i]);
				}
				if (tokens[i - 1].equals("new-firstname")) {
					changeFirstName(username, tokens[i]);
				}
				if (tokens[i - 1].equals("new-lastname")) {
					changeLastName(username, tokens[i]);
				}
				if (tokens[i - 1].equals("new-email")) {
					changeEmail(username, tokens[i]);
				}
			}
			result = "successful update";
		} else {
			result = "unsuccesful update";
		}
		return result;
	}

	public String logout(String currSessionID, String currUsername, SocketChannel socketChannel) {
		if (!isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (currUsername.equals(getSessions().get(currSessionID).getUsername())) {
			getSessions().remove(currSessionID);
			getUsers().get(currUsername).setSession(null);
			result = "login again";
		} else {
			result = "unsuccessful logout";
		}
		return result;
	}

	public String addAdmin(String currUsername, String usernameToMakeAdmin, String currSessionID,
			SocketChannel socketChannel) {
		getLogger().writeConfigChageStart(socketChannel, currUsername, usernameToMakeAdmin,
				Command.ADD_ADMIN.getCommand());
		if (!isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (getAdmins().contains(currUsername)
				&& getSessions().get(currSessionID).getUsername().equals(currUsername)) {
			getAdmins().add(usernameToMakeAdmin);
			getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToMakeAdmin, true);
			result = "successfully made admin";
		} else {
			getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToMakeAdmin, false);
			result = "unsuccessfully made admin";
		}
		return result;
	}

	public String removeAdmin(String currUsername, String usernameToRemoveAdmin, String currSessionID,
			SocketChannel socketChannel) {
		OutputHandler output = new OutputHandler(socketChannel);
		getLogger().writeConfigChageStart(socketChannel, currUsername, usernameToRemoveAdmin,
				Command.REMOVE_ADMIN.getCommand());
		if (!isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (getAdmins().contains(currUsername)
				&& getSessions().get(currSessionID).getUsername().equals(currUsername)) {
			getAdmins().remove(usernameToRemoveAdmin);
			getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToRemoveAdmin, true);
			result = "successfully removed admin";
		} else {
			getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToRemoveAdmin, false);
			result = "unsuccessfully removed admin";
		}
		return result;
	}

	public String deleteUser(String currUsername, String usernameToDelete, String currSessionID,
			SocketChannel socketChannel) {
		getLogger().writeConfigChageStart(socketChannel, currUsername, usernameToDelete,
				Command.DELETE_USER.getCommand());
		if (!isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (getAdmins().contains(currUsername)
				&& getSessions().get(currSessionID).getUsername().equals(currUsername)) {
			String userToDeleteSession = users.get(usernameToDelete).getSessionID();
			users.remove(usernameToDelete);
			if (userToDeleteSession != null) {
				sessions.get(userToDeleteSession).setUsername(null);
				users.get(usernameToDelete).setSession(null);
			}
			getAdmins().remove(usernameToDelete);
			getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToDelete, true);
			getUserFileEditor().deleteUserFromFile(usernameToDelete);
			result = "succesfully deleted user";
		} else {
			getLogger().writeConfigChageFinish(socketChannel, currUsername, usernameToDelete, false);
			result = "unsuccessfully deleted user";
		}
		return result;
	}
	
}
