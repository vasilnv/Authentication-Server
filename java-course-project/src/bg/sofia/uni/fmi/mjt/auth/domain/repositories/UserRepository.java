package bg.sofia.uni.fmi.mjt.auth.domain.repositories;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import bg.sofia.uni.fmi.mjt.auth.FileEditors.UserFileEditor;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class UserRepository {
	private static final String WRITER_ERROR_MESSAGE = "Problem with the File Writer";
	private static final String READER_ERROR_MESSAGE = "Problem with the Reader";
	private static final int TIME_TO_LOCK = 50;

	private Map<String, AuthenticatedUser> users = new ConcurrentHashMap<>();
	private Set<String> admins = new HashSet<>();

	private UserFileEditor userFileEditor = new UserFileEditor();

	public UserRepository() {

	}

	public AuthenticatedUser getUser(String username) {
		return users.get(username);
	}

	public Set<String> getAdmins() {
		return admins;
	}

	public UserFileEditor getUserFileEditor() {
		return userFileEditor;
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
		this.userFileEditor.deleteUserFromFile(username);
		return username;
	}

	public String updateUsername(String oldUsername, String newUsername) {
		users.get(oldUsername).setUsername(newUsername);
		AuthenticatedUser tmpUser = users.get(oldUsername);
		users.remove(oldUsername);
		users.put(newUsername, tmpUser);
		userFileEditor.changeConfiguration(oldUsername, newUsername, 0);
		return newUsername;
	}

	public String updateFirstName(String username, String firstName) {
		users.get(username).setFirstName(firstName);
		userFileEditor.changeConfiguration(username, firstName, 2);
		return firstName;
	}

	public String updateLastName(String username, String lastName) {
		users.get(username).setLastName(lastName);
		userFileEditor.changeConfiguration(username, lastName, 3);
		return lastName;
	}

	public String updateEmail(String username, String email) {
		users.get(username).setEmail(email);
		userFileEditor.changeConfiguration(username, email, 4);
		return email;
	}

	public String updatePassword(String username, String password) {
		users.get(username).setPassword(password);
		userFileEditor.changeConfiguration(username, password, 1);
		return password;
	}

	public void addUser(String username, AuthenticatedUser user) {
		this.users.put(username, user);
	}

	public void writeUserInFile(String username, AuthenticatedUser user) {
		this.userFileEditor.writeUserInFile(user);
	}

	public String getUserPassword(String username) {
		return this.users.get(username).getPassword();
	}

	public void load() {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader("users.txt"))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				String[] tokens = line.split(" ");
				int index = 0;
				String username = tokens[index++];
				String password = tokens[index++];
				String firstname = tokens[index++];
				String lastname = tokens[index++];
				String email = tokens[index++];
				AuthenticatedUser user = new AuthenticatedUser(username, password, firstname, lastname, email);
				addUser(username, user);

				if (this.admins.isEmpty()) {
					this.admins.add(tokens[0]);
				}
				line = bufferedReader.readLine();
			}

		} catch (FileNotFoundException e) {
			System.out.println(READER_ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(WRITER_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public boolean checkIfUserIsAdmin(String username) {
		return this.admins.contains(username);
	}

	public void removeAdmin(String username) {
		this.admins.remove(username);
	}

	public void addAdmin(String username) {
		this.admins.add(username);
	}

	public void checkIfAnyAdminsExist(String username) {
		if (this.getAdmins().isEmpty()) {
			addAdmin(username);
		}
	}

}
