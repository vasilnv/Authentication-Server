package bg.sofia.uni.fmi.mjt.auth;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import bg.sofia.uni.fmi.mjt.auth.FileEditors.AuditLog;
import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.UserUpdater;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;

public class TestDomain {

	
	@Mock
	private UserRepository userRepository;
	@Mock
	private SessionRepository sessionRepository;
	@Mock
	private AuthenticatedUser user;
	@Mock
	private Map<SocketChannel, String> channelsByUsername;
	@Mock
	private UserUpdater userUpdater;
	@Mock
	private AuditLog logger;
	
	@InjectMocks
	private Domain domain;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testRegisterInSystemUnsuccessfully() throws IOException {
		SocketChannel channel = SocketChannel.open();
		when(user.getUsername()).thenReturn("gogo");
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		
		String result = domain.registerInSystem(channel, user);
		
		assertEquals("unsuccessful registration", result);
	}

	@Test
	public void testRegisterInSystemSuccessfully() throws IOException {
		SocketChannel channel = SocketChannel.open();
		when(user.getUsername()).thenReturn("gogo");
		when(userRepository.checkIfUserExists("gogo")).thenReturn(false);
		
		String result = domain.registerInSystem(channel, user);
		
		assertTrue(result.contains("completed registration"));
	}	
	
	
	@Test
	public void testUserLogInByNameAndPasswordUnSuccessul() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		when(userRepository.checkIfUserExists("gogog")).thenReturn(false);
		when(userRepository.checkIfUserIsBlocked("gogog")).thenReturn(false);

		String message = domain.logInByNameAndPassword("gogog", "1234", socketChannel);

		assertTrue(message.contains("unsuccessful login"));
	}

	@Test
	public void testUserLogInByNameAndPasswordUnSuccessulBecauseUserIsBlocked() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		when(userRepository.checkIfUserExists("gogog")).thenReturn(true);
		when(userRepository.checkIfUserIsBlocked("gogog")).thenReturn(true);

		String message = domain.logInByNameAndPassword("gogog", "1234", socketChannel);

		assertTrue(message.contains("You are blocked"));
	}

	@Test
	public void testLogInUnSuccessfullyBecauseUserIsBlocked() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		when(userRepository.getUser("gogog")).thenReturn(user);
		when(userRepository.getUser("gogog").getLoginFailed()).thenReturn(3);
		
		String result = domain.logInUnSuccessfully("gogog", socketChannel);
		
		assertTrue(result.contains("You are blocked"));
	}

	@Test
	public void testLogInUnSuccessfully() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		when(userRepository.getUser("gogog")).thenReturn(user);
		when(userRepository.getUser("gogog").getLoginFailed()).thenReturn(2);
		
		String result = domain.logInUnSuccessfully("gogog", socketChannel);
		
		assertEquals(result,"unsuccessful login");
	}	
	
	@Test
	public void testLogInSuccessfully() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		Session session = new Session();
		when(userRepository.getUser("gogog")).thenReturn(user);
		when(sessionRepository.getSession(anyString())).thenReturn(session);
		String result = domain.logInSuccessfully("gogog", socketChannel);
		
		assertTrue(result.contains("successful login with"));
	}	
	
	@Test
	public void testUserLogInByNameAndPasswordSuccessul() throws IOException {
		String password = "1234";
		SocketChannel channel = SocketChannel.open();
		Session session = new Session();
		when(userRepository.checkIfUserExists("gogog")).thenReturn(true);
		when(userRepository.checkIfUserIsBlocked("gogog")).thenReturn(false);
		when(userRepository.getUserPassword("gogog")).thenReturn(password);
		when(userRepository.getUser("gogog")).thenReturn(user);
		when(sessionRepository.getSession(anyString())).thenReturn(session);

		String message = domain.logInByNameAndPassword("gogog", password, channel);

		assertTrue(message.contains("successful login with"));
	}

	@Test
	public void testUserLogInByNameAndPasswordUnSuccessulBecauseOfWrongPassword() throws IOException {
		String password = "1234";
		SocketChannel channel = SocketChannel.open();
		when(userRepository.checkIfUserExists("gogog")).thenReturn(true);
		when(userRepository.checkIfUserIsBlocked("gogog")).thenReturn(false);
		when(userRepository.getUserPassword("gogog")).thenReturn("12");
		when(userRepository.getUser("gogog")).thenReturn(user);

		String message = domain.logInByNameAndPassword("gogog", password, channel);

		assertTrue(message.contains("unsuccessful login"));
	}
	
	@Test
	public void testUserLogInBySessionUnsuccessful() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(false);
		
		String result = domain.logInBySession("1", socketChannel);
		
		assertEquals("unsuccessful login", result);
	}

	@Test
	public void testUserLogInBySessionSuccessful() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		String sessionID = "123";
		when(sessionRepository.isUserLoggedIn(sessionID)).thenReturn(true);
		when(sessionRepository.getSessionUsername(sessionID)).thenReturn("gogo");
		when(userRepository.getUser("gogo")).thenReturn(user);
		
		String result = domain.logInBySession(sessionID, socketChannel);
		
		assertTrue(result.contains("successful login with "));
	}
	
	@Test
	public void testResetPasswordNotLoggedIn() throws IOException {
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(false);
		
		String result = domain.resetPassword("1", "gogo", "1234", "12345", SocketChannel.open());
		
		assertEquals("not logged in", result);
	}

	@Test
	public void testResetPasswordSuccessfully() throws IOException {
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.getUser("gogo")).thenReturn(user);
		when(user.getPassword()).thenReturn("1234");
		when(channelsByUsername.get(anyObject())).thenReturn("gogo");
		
		String result = domain.resetPassword("1", "gogo", "1234", "12345", SocketChannel.open());
		
		assertEquals("successfully changed password", result);
	}

	@Test
	public void testResetPasswordUnsuccessfullyWhenUserDoesntExists() throws IOException {
		when(userRepository.checkIfUserExists("gogo")).thenReturn(false);
		
		String result = domain.resetPassword("1", "gogo", "1234", "12345", SocketChannel.open());
		
		assertEquals("unsuccessfully changed password", result);
	}

	@Test
	public void testResetPasswordUnsuccessfullyWhenOldPasswordIsTypedWrong() throws IOException {
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.getUser("gogo")).thenReturn(user);
		when(user.getPassword()).thenReturn("12345");
		when(channelsByUsername.get(anyObject())).thenReturn("gogo");
		
		String result = domain.resetPassword("1", "gogo", "1234", "12345", SocketChannel.open());
		
		assertEquals("unsuccessfully changed password", result);
	}
	
	@Test
	public void testUpdateUserUnsuccessfullyWhenNotLoggedIn() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(false);
		
		String result = domain.updateUser("1", "gogo", "new-username", "12345");
		
		assertEquals("not logged in", result);
	}

	@Test
	public void testUpdateUserSuccessfullyWithNewUsername() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(userRepository.getUser("gogo")).thenReturn(user);
		when(user.getSessionID()).thenReturn("1");
		when(userRepository.getUser("gogo1")).thenReturn(user);
		
		String result = domain.updateUser("1", "gogo", "new-username", "gogo1");
		
		assertEquals("successful update", result);
	}

	@Test
	public void testUpdateUserSuccessfullyWithNewFirstname() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(userRepository.getUser("gogo")).thenReturn(user);
		when(user.getSessionID()).thenReturn("1");
		when(userRepository.getUser("gogo1")).thenReturn(user);
		
		String result = domain.updateUser("1", "gogo", "new-firstname", "gogo1");
		
		assertEquals("successful update", result);
	}

	@Test
	public void testUpdateUserSuccessfullyWithNewLastname() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(userRepository.getUser("gogo")).thenReturn(user);
		when(user.getSessionID()).thenReturn("1");
		when(userRepository.getUser("gogo1")).thenReturn(user);
		
		String result = domain.updateUser("1", "gogo", "new-lastname", "gogo1");
		
		assertEquals("successful update", result);
	}

	@Test
	public void testUpdateUserSuccessfullyWithNewEmail() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(userRepository.getUser("gogo")).thenReturn(user);
		when(user.getSessionID()).thenReturn("1");
		when(userRepository.getUser("gogo1")).thenReturn(user);
		
		String result = domain.updateUser("1", "gogo", "new-email", "gogo1");
		
		assertEquals("successful update", result);
	}

	@Test
	public void testUpdateUserUnsuccessfullyWhenUserDoesntExists() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserExists("gogo")).thenReturn(false);
		
		String result = domain.updateUser("1", "gogo", "new-email", "gogo1");
		
		assertEquals("unsuccessful update", result);
	}

	@Test
	public void testUpdateUserUnsuccessfullyWhenUserDoesntHaveActiveSession() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserExists("gogo")).thenReturn(true);
		when(userRepository.getUser("gogo")).thenReturn(user);
		when(user.getSessionID()).thenReturn("2");
		
		String result = domain.updateUser("1", "gogo", "new-email", "gogo1");
		
		assertEquals("unsuccessful update", result);
	}

	@Test
	public void testLogoutWhenUserNotLoggedIn() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(false);
		
		String result = domain.logout("1", "gogo");
		
		assertEquals("not logged in", result);
	}

	@Test
	public void testLogoutWhenUsernameNotRight() {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(sessionRepository.getSessionUsername("1")).thenReturn("gogo");
		
		
		String result = domain.logout("1", "gogo1");
		
		assertEquals("unsuccessful logout", result);
	}

	@Test
	public void testLogoutSuccess() {
		Map<String, Session> sessions = new HashMap<>();
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(sessionRepository.getSessionUsername("1")).thenReturn("gogo");
		when(sessionRepository.getSessions()).thenReturn(sessions);
		when(userRepository.getUser("gogo")).thenReturn(user);
		
		String result = domain.logout("1", "gogo");
		
		assertEquals("login again", result);
	}

	@Test
	public void testAddAdminWhenUserNotLoggedIn() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(false);
		
		String result = domain.addAdmin("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("not logged in", result);
	}

	@Test
	public void testAddAdminWhenUserDoesNotHaveAdminRights() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(false);
		
		String result = domain.addAdmin("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("unsuccessfully made admin", result);
	}

	@Test
	public void testAddAdminWhenSessionIsWrong() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(true);
		when(sessionRepository.getSessionUsername("1")).thenReturn("gogo");
		
		String result = domain.addAdmin("gogo1", "tosho", "1", SocketChannel.open());
		
		assertEquals("unsuccessfully made admin", result);
	}

	@Test
	public void testAddAdminSuccessfully() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(true);
		when(sessionRepository.getSessionUsername("1")).thenReturn("gogo");
		
		String result = domain.addAdmin("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("successfully made admin", result);
	}
	
	@Test
	public void testRemoveAdminWhenUserNotLoggedIn() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(false);
		
		String result = domain.removeAdmin("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("not logged in", result);
	}
	
	@Test
	public void testRemoveAdminWhenUserDoesNotHaveAdminRights() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(false);
		
		String result = domain.removeAdmin("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("unsuccessfully removed admin", result);
	}

	@Test
	public void testRemoveAdminSuccessfully() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(true);
		when(sessionRepository.getSessionUsername("1")).thenReturn("gogo");
		
		String result = domain.removeAdmin("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("successfully removed admin", result);
	}

	@Test
	public void testDeleteUserWhenUserNotLoggedIn() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(false);
		
		String result = domain.deleteUser("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("not logged in", result);
	}
	
	@Test
	public void testDeleteUserWhenUserDoesNotHaveAdminRights() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(false);
		
		String result = domain.deleteUser("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("unsuccessfully deleted user", result);
	}

	@Test
	public void testDeleteUserSuccessfully() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(true);
		when(sessionRepository.getSessionUsername("1")).thenReturn("gogo");
		when(userRepository.getUser("tosho")).thenReturn(user);
		when(user.getSessionID()).thenReturn("1");
		
		String result = domain.deleteUser("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("successfully deleted user", result);
	}

	@Test
	public void testDeleteUserSuccessfullyWhenDeletedUserIsNotLoggedIn() throws IOException {
		when(sessionRepository.isUserLoggedIn("1")).thenReturn(true);
		when(userRepository.checkIfUserIsAdmin("gogo")).thenReturn(true);
		when(sessionRepository.getSessionUsername("1")).thenReturn("gogo");
		when(userRepository.getUser("tosho")).thenReturn(user);
		when(user.getSessionID()).thenReturn(null);
		
		String result = domain.deleteUser("gogo", "tosho", "1", SocketChannel.open());
		
		assertEquals("successfully deleted user", result);
	}
}
