package bg.sofia.uni.fmi.mjt.auth.domain;

import bg.sofia.uni.fmi.mjt.auth.domain.repositories.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.UserRepository;

public class UserUpdater {
	private UserRepository userRepository;
	private SessionRepository sessionRepository;

	public UserUpdater(UserRepository userRepository, SessionRepository sessionRepository) {
		this.userRepository = userRepository;
		this.sessionRepository = sessionRepository;
	}

	public String changeUsername(String oldUsername, String newUsername) {
		userRepository.updateUsername(oldUsername, newUsername);
		String oldSession = userRepository.getUser(newUsername).getSessionID();
		sessionRepository.getSession(oldSession).setUsername(newUsername);
		return newUsername;
	}

	public String changeFirstName(String username, String firstName) {
		userRepository.updateFirstName(username, firstName);
		return firstName;
	}

	public String changeLastName(String username, String lastName) {
		userRepository.updateLastName(username, lastName);
		return lastName;
	}

	public String changeEmail(String username, String email) {
		userRepository.updateEmail(username, email);
		return email;
	}

	public String changePassword(String username, String password) {
		userRepository.updatePassword(username, password);
		return password;
	}

}
