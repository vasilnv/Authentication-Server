package bg.sofia.uni.fmi.mjt.auth.domain.users;

import java.time.LocalDateTime;

public class AuthenticatedUser {
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private int timesLoginFailed = 0;
	private LocalDateTime blockedTime;
	private boolean isBlocked = false;
	private String sessionID;

	public AuthenticatedUser(String username, String pass, String firstName, String lastName, String email) {
		super();
		this.username = username;
		this.password = pass;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		System.out.println("user created");
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public int getLoginFailed() {
		return this.timesLoginFailed;
	}

	public LocalDateTime getBlockingTime() {
		return this.blockedTime;
	}

	public boolean getIsBlocked() {
		return this.isBlocked;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public void setFirstName(String name) {
		this.firstName = name;
	}

	public void setLastName(String name) {
		this.lastName = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSession(String session) {
		this.sessionID = session;
	}

	public void incrementloginFailed() {
		this.timesLoginFailed++;
	}

	public void block() {
		this.isBlocked = true;
		blockedTime = LocalDateTime.now();
	}

	public void unblock() {
		this.isBlocked = false;
		this.timesLoginFailed = 0;
	}

	@Override
	public String toString() {
		return this.username + " " + this.password + " " + this.firstName + " " + this.lastName + " " + this.email;
	}

}
