package bg.sofia.uni.fmi.mjt.auth.domain.repositories;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class UserRepository {
	private Map<String, AuthenticatedUser> users = new ConcurrentHashMap<>();
	private static final int TIME_TO_LOCK = 50;

	public UserRepository()	{
		
	}
	
	
	public AuthenticatedUser getUser(String username) {
		return users.get(username);
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
	
	public boolean checkIfUserExists(String name) {
		return users.containsKey(name);
	}
	
	public String deleteUser(String username) {
		users.remove(username);
		return username;
	}
	
	public String updateUsername(String oldUsername, String newUsername) {
		users.get(oldUsername).setUsername(newUsername);
		AuthenticatedUser tmpUser = users.get(oldUsername);
		users.remove(oldUsername);
		users.put(newUsername, tmpUser);
		return newUsername;
	}
	
	public String updateFirstName(String username, String firstName) {
		users.get(username).setFirstName(firstName);
		return firstName;
	}
	
	public String updateLastName(String username, String lastName) {
		users.get(username).setLastName(lastName);
		return lastName;
	}
	
	public String updateEmail(String username, String email) {
		users.get(username).setEmail(email);
		return email;
	}
	
	public String updatePassword(String username, String password) {
		users.get(username).setPassword(password);
		return password;
	}
	
	public void addUser(String username, AuthenticatedUser user) {
		this.users.put(username, user);
	}
	public String getUserPassword(String username) {
		return this.users.get(username).getPassword();
	}

}
