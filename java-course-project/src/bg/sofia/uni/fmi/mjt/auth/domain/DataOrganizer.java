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
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class DataOrganizer {
	private static final int ZERO_ARG = 0;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FOURTH_ARG = 4;
	private static final int TIME_TO_LOCK = 50;

	private UserFileEditor userFileEditor = new UserFileEditor();
	private AuditLog logger = new AuditLog();

	private Map<SocketChannel, String> channelsByUsername = new HashMap<>();
	private Map<String, SocketChannel> usernamesByChannel = new HashMap<>();

	private Map<String, Session> sessions = new ConcurrentHashMap<>(); // sessionID - session
	private Map<String, String> usersSessions = new ConcurrentHashMap<>(); // username - sessionID
	private Map<String, String> sessionsUsers = new ConcurrentHashMap<>(); // sessionID - username

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
	public Map<String, String> getUsersSessions() {
		return usersSessions;
	}
	public Map<String, String> getSessionsUsers() {
		return sessionsUsers;
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

	public void removeSession(String sessionID) {
		sessions.remove(sessionID);
		String usernameSession = sessionsUsers.get(sessionID);
		sessionsUsers.remove(sessionID);
		usersSessions.remove(usernameSession);
		SocketChannel channel = usernamesByChannel.get(usernameSession);
		OutputHandler output = new OutputHandler(channel);
		output.write("Your session expired. Please login again.");
	}

	public void addUserSession(String username, Session session) {
		usersSessions.put(username, session.getId());
	}

	public void addSessionUser(String username, Session session) {
		sessionsUsers.put(session.getId(), username);
	}

	public void changeUsername(String oldUsername, String newUsername) {
		users.get(oldUsername).setUsername(newUsername);
		AuthenticatedUser tmpUser = users.get(oldUsername);
		users.remove(oldUsername);
		users.put(newUsername, tmpUser);
		String oldSession = usersSessions.get(oldUsername);
		usersSessions.remove(oldUsername);
		usersSessions.put(newUsername, oldSession);
		sessionsUsers.put(oldSession, newUsername);
		userFileEditor.changeConfiguration(oldUsername, newUsername, ZERO_ARG);

	}

	public void changeFirstName(String username, String firstName) {
		users.get(username).setFirstName(firstName);
		userFileEditor.changeConfiguration(username, firstName, SECOND_ARG);
	}

	public void changeLastName(String username, String lastName) {
		users.get(username).setLastname(lastName);
		userFileEditor.changeConfiguration(username, lastName, THIRD_ARG);
	}

	public void changeEmail(String username, String email) {
		users.get(username).setEmail(email);
		userFileEditor.changeConfiguration(username, email, FOURTH_ARG);
	}

	public boolean isUserLoggedIn(String sessionId) {
		return (sessions.containsKey(sessionId));
	}


}
