package bg.sofia.uni.fmi.mjt.auth.domain;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import bg.sofia.uni.fmi.mjt.auth.FileEditors.AuditLog;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.Command;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandParameter;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.handler.OutputHandler;

public class Domain {
	private static Domain instance;

	private AuditLog logger = new AuditLog();

	private UserRepository userRepository = new UserRepository();
	private SessionRepository sessionRepository = new SessionRepository();
	private UserUpdater userUpdater = new UserUpdater(userRepository, sessionRepository);

	private Map<SocketChannel, String> channelsByUsername = new HashMap<>();
	private Map<String, SocketChannel> usernamesByChannel = new HashMap<>();

	private Domain() {

	}

	public static Domain getInstance() {
		if (instance == null) {
			instance = new Domain();
		}
		return instance;
	}

	public SessionRepository getSessionRepository() {
		return this.sessionRepository;
	}

	public UserRepository getUserRepository() {
		return this.userRepository;
	}

	public Map<SocketChannel, String> getChannelsByUsername() {
		return this.channelsByUsername;
	}

	public void loadUsers() {
		userRepository.load();
	}

	public void removeSession(Session session) {
		sessionRepository.removeSession(session);
		String usernameSession = session.getUsername();
		session.setUsername(null);
		userRepository.getUser(usernameSession).setSession(null);
		SocketChannel channel = usernamesByChannel.get(usernameSession);
		OutputHandler output = new OutputHandler(channel);
		output.write("Your session expired. Please login again.");
	}

	private String result;

	public String registerInSystem(SocketChannel socketChannel, AuthenticatedUser newUser) {
		String username = newUser.getUsername();
		if (!userRepository.checkIfUserExists(username)) {
			Session session = new Session();
			sessionRepository.addSession(session);
			sessionRepository.mapSessionUser(session, newUser);
			System.out.println("user registered with session ID :" + session.getId());

			this.channelsByUsername.put(socketChannel, username);
			this.usernamesByChannel.put(username, socketChannel);
			userRepository.addUser(username, newUser);
			userRepository.writeUserInFile(username, newUser);
			userRepository.checkIfAnyAdminsExist(username);
			result = "completed registration with sessionId: " + session.getId();
		} else {
			result = "unsuccessful registration";
		}
		return result;
	}
	
	public String logInByNameAndPassword(String username, String password, SocketChannel socketChannel) {
		if (userRepository.checkIfUserExists(username) && userRepository.checkIfUserIsBlocked(username)) {
			result = "You are blocked";
		} else if (userRepository.checkIfUserExists(username)
				&& userRepository.getUserPassword(username).equals(password)) {
			result = logInSuccessfully(username, socketChannel);
		} else if (userRepository.checkIfUserExists(username)) {
			result = logInUnSuccessfully(username, socketChannel);
		} else {
			this.logger.writeFailedLogin(socketChannel, username);
			result = "unsuccessful login";
		}
		return result;
	}

	public String logInSuccessfully(String username, SocketChannel socketChannel) {
		Session session = new Session();
		sessionRepository.addSession(session);
		userRepository.getUser(username).setSession(session.getId());
		sessionRepository.getSession(session.getId()).setUsername(username);
		this.channelsByUsername.put(socketChannel, username);
		this.usernamesByChannel.put(username, socketChannel);

		result = "successful login with sessionID: " + session.getId();
		return result;
	}

	public String logInUnSuccessfully(String username, SocketChannel socketChannel) {
		userRepository.getUser(username).incrementloginFailed();
		if (userRepository.getUser(username).getLoginFailed() == 3) {
			userRepository.getUser(username).block();
			this.logger.writeFailedLogin(socketChannel, username);
			result = "You are blocked please try again in 50 seconds";
		}
		else {
			result = "unsuccessful login";
		}
		return result;
	}

	public String logInBySession(String sessionId, SocketChannel socketChannel) {
		if (sessionRepository.isUserLoggedIn(sessionId)) {
			String username = sessionRepository.getSessionUsername(sessionId);
			this.channelsByUsername.put(socketChannel, username);
			this.usernamesByChannel.put(username, socketChannel);
			userRepository.getUser(username).setSession(sessionId);
			sessionRepository.setSessionUsername(sessionId, username);

			result = "successful login with sessionID: " + sessionId;
		} else {
			result = "unsuccessful login";
		}
		return result;
	}

	public String resetPassword(String userSession, String username, String oldPassword, String newPassword,
			SocketChannel socketChannel) {
		if (userRepository.checkIfUserExists(username) && !sessionRepository.isUserLoggedIn(userSession)) {
			result = "not logged in";
		} else if (userRepository.checkIfUserExists(username)
				&& this.channelsByUsername.get(socketChannel).equals(username)
				&& userRepository.getUser(username).getPassword().equals(oldPassword)) {
			userUpdater.changePassword(username, newPassword);
			result = "succesfully changed password";
		} else {
			result = "unsuccessfully changed password";
		}
		return result;
	}

	public String updateUser(String currSessionID, String username, String command, String updatedData) {
		if (!sessionRepository.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (sessionRepository.isUserLoggedIn(currSessionID)
				&& userRepository.getUser(username).getSessionID().equals(currSessionID)) {
			if (command.equals(CommandParameter.NEW_USERNAME.getCommand())) {
				userUpdater.changeUsername(username, updatedData);
			}
			if (command.equals(CommandParameter.NEW_FIRSTNAME.getCommand())) {
				userUpdater.changeFirstName(username, updatedData);
			}
			if (command.equals(CommandParameter.NEW_LASTNAME.getCommand())) {
				userUpdater.changeLastName(username, updatedData);
			}
			if (command.equals(CommandParameter.NEW_EMAIL.getCommand())) {
				userUpdater.changeEmail(username, updatedData);
			}
			result = "successful update";
		} else {
			result = "unsuccesful update";
		}
		return result;
	}

	public String logout(String currSessionID, String currUsername, SocketChannel socketChannel) {
		if (!sessionRepository.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (currUsername.equals(sessionRepository.getSessionUsername(currSessionID))) {
			sessionRepository.getSessions().remove(currSessionID);
			userRepository.getUser(currUsername).setSession(null);
			result = "login again";
		} else {
			result = "unsuccessful logout";
		}
		return result;
	}

	public String addAdmin(String currUsername, String usernameToMakeAdmin, String currSessionID,
			SocketChannel socketChannel) {
		this.logger.writeConfigChangeStart(socketChannel, currUsername, usernameToMakeAdmin,
				Command.ADD_ADMIN.getCommand());
		if (!sessionRepository.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (userRepository.checkIfUserIsAdmin(currUsername)
				&& sessionRepository.getSessionUsername(currSessionID).equals(currUsername)) {
			userRepository.addAdmin(usernameToMakeAdmin);
			this.logger.writeConfigChangeFinish(socketChannel, currUsername, usernameToMakeAdmin, true);
			result = "successfully made admin";
		} else {
			this.logger.writeConfigChangeFinish(socketChannel, currUsername, usernameToMakeAdmin, false);
			result = "unsuccessfully made admin";
		}
		return result;
	}

	public String removeAdmin(String currUsername, String usernameToRemoveAdmin, String currSessionID,
			SocketChannel socketChannel) {
		this.logger.writeConfigChangeStart(socketChannel, currUsername, usernameToRemoveAdmin,
				Command.REMOVE_ADMIN.getCommand());
		if (!sessionRepository.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (userRepository.checkIfUserIsAdmin(currUsername)
				&& sessionRepository.getSessionUsername(currSessionID).equals(currUsername)) {
			userRepository.removeAdmin(usernameToRemoveAdmin);
			this.logger.writeConfigChangeFinish(socketChannel, currUsername, usernameToRemoveAdmin, true);
			result = "successfully removed admin";
		} else {
			this.logger.writeConfigChangeFinish(socketChannel, currUsername, usernameToRemoveAdmin, false);
			result = "unsuccessfully removed admin";
		}
		return result;
	}

	public String deleteUser(String currUsername, String usernameToDelete, String currSessionID,
			SocketChannel socketChannel) {
		this.logger.writeConfigChangeStart(socketChannel, currUsername, usernameToDelete,
				Command.DELETE_USER.getCommand());
		if (!sessionRepository.isUserLoggedIn(currSessionID)) {
			result = "not logged in";
		} else if (userRepository.checkIfUserIsAdmin(currUsername)
				&& sessionRepository.getSessionUsername(currSessionID).equals(currUsername)) {
			String userToDeleteSession = userRepository.getUser(usernameToDelete).getSessionID();
			userRepository.deleteUser(usernameToDelete);
			if (userToDeleteSession != null) {
				sessionRepository.setSessionUsername(userToDeleteSession, null);
				userRepository.getUser(usernameToDelete).setSession(null);
			}
			userRepository.removeAdmin(usernameToDelete);
			this.logger.writeConfigChangeFinish(socketChannel, currUsername, usernameToDelete, true);
			result = "succesfully deleted user";
		} else {
			this.logger.writeConfigChangeFinish(socketChannel, currUsername, usernameToDelete, false);
			result = "unsuccessfully deleted user";
		}
		return result;
	}
}
