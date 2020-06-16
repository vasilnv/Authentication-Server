package bg.sofia.uni.fmi.mjt.auth;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;
import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;

public class TestDomain {
	@Test
	public void testIfAddsSessionsCorrectly() {
		Session mockSession = mock(Session.class);
		when(mockSession.getId()).thenReturn("1");
		Domain data = new Domain();
		data.addSession(mockSession);
		assertTrue(data.getSessions().containsKey(mockSession.getId()));
	}

	@Test
	public void testIfAddsUsersSessionsCorrectly() {
		Session session = mock(Session.class);
		String username = "gogo";
		when(session.getId()).thenReturn("1");
		Domain data = new Domain();
		data.addUserSession(username, session);
		assertTrue(data.getUsersSessions().containsKey(username));
	}

	@Test
	public void testIfAddsSessionsUsersCorrectly() {
		Session session = mock(Session.class);
		String username = "gogo";
		when(session.getId()).thenReturn("1");
		Domain data = new Domain();
		data.addSessionUser(username, session);
		assertTrue(data.getSessionsUsers().containsKey(session.getId()));
	}

	@Test
	public void testRegisterUnsuccessful() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.checkIfUserExists("gogo")).thenReturn(true);
		AuthenticatedUser newUser = new AuthenticatedUser("gogo", "gogo", "gogo", "gogo", "gogo");
		String message = domainModel.registerInSystem(socketChannel, newUser);
		assertEquals(message, "unsuccessful registration");
	}

	@Test
	public void testRegister() throws IOException {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "gogo", "gogo", "gogo", "gogo");
		Domain data = new Domain();
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("1");
		data.addSession(session);
		data.addUserSession("gogo", session);
		data.addSessionUser("gogo", session);
		Map<String, String> usersSessions = new HashMap<>();
		usersSessions.put("gogo", "1");
		assertEquals(usersSessions.get("gogo"), data.getUsersSessions().get("gogo"));

	}

	@Test
	public void testRegisterSuccessful() throws IOException {
		Domain data = new Domain();
		SystemFacade domain = SystemFacade.getInstance(data);
		AuthenticatedUser user = new AuthenticatedUser("gogo", "gogo", "gogo", "gogo", "gogo");
		SocketChannel socketChannel = SocketChannel.open();
		String message = domain.registerInSystem(socketChannel, user);
		assertTrue(message.contains("completed registration"));
	}

	@Test
	public void isUserLoggedInSuccessful() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		AuthenticatedUser newUser = new AuthenticatedUser("gogo", "gogo", "gogo", "gogo", "gogo");
		when(dataOrg.checkIfUserExists("gogo")).thenReturn(true);
		when(dataOrg.checkIfUserIsBlocked("gogo")).thenReturn(false);
		Map<String, AuthenticatedUser> users = new HashMap<>();
		users.put("gogo", newUser);
		when(dataOrg.getUsers()).thenReturn(users);
		HashMap<String, Session> sessions = new HashMap<>();
		Session session = new Session();
		sessions.put("0", session);

		doNothing().when(dataOrg).addSession(session);
		String message = domainModel.logInByNameAndPass("gogo", "gogo", socketChannel);
		assertTrue(message.contains("successful login with"));

	}

	@Test
	public void isUserLoggedInUnSuccessful() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		AuthenticatedUser newUser = new AuthenticatedUser("gogo", "gogo", "gogo", "gogo", "gogo");
		when(dataOrg.checkIfUserExists("gogo")).thenReturn(false);
		when(dataOrg.checkIfUserIsBlocked("gogo")).thenReturn(false);
		Map<String, AuthenticatedUser> users = new HashMap<>();
		users.put("gogo", newUser);
		when(dataOrg.getUsers()).thenReturn(users);
		HashMap<String, Session> sessions = new HashMap<>();
		Session session = new Session();
		sessions.put("0", session);

		doNothing().when(dataOrg).addSession(session);
		String message = domainModel.logInByNameAndPass("gogo", "gogo", socketChannel);
		assertTrue(message.contains("unsuccessful login"));

	}

	
	@Mock
	private Domain dataOrg;

	@InjectMocks
	private SystemFacade domainModel;

	@Test
	public void isUserLoggedInUnSuccessul() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.checkIfUserExists("gogog")).thenReturn(false);
		when(dataOrg.checkIfUserIsBlocked("gogog")).thenReturn(false);
		AuditLog logger = new AuditLog();
		when(dataOrg.getLogger()).thenReturn(logger);
		String message = domainModel.logInByNameAndPass("gogog", "gogo", socketChannel);
		assertTrue(message.contains("unsuccessful login"));

	}

	@Test
	public void isUserBlockedSuccess() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.checkIfUserExists("gogog")).thenReturn(true);
		when(dataOrg.checkIfUserIsBlocked("gogog")).thenReturn(true);
		String message = domainModel.logInByNameAndPass("gogog", "gogo", socketChannel);

		assertTrue(message.contains("You are blocked"));

	}

	@Test
	public void isUserSessionLoginSuccessful() throws IOException {
		MockitoAnnotations.initMocks(this);
		HashMap<String, Session> sessions = new HashMap<>();
		Session session = new Session();
		sessions.put("1", session);

		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.getSessions()).thenReturn(sessions);
		String message = domainModel.logInBySession("1", socketChannel);
		assertTrue(message.contains("successful login with"));

	}

	@Test
	public void isUserSessionLoginFailed() throws IOException {
		MockitoAnnotations.initMocks(this);
		HashMap<String, Session> sessions = new HashMap<>();
		Session session = new Session();
		sessions.put("0", session);

		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.getSessions()).thenReturn(sessions);
		String message = domainModel.logInBySession("1", socketChannel);
		assertFalse(message.contains("successful login with"));

	}

	@Test
	public void testResetPasswordNotLoggedIn() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.checkIfUserExists("gogo")).thenReturn(true);
		when(dataOrg.isUserLoggedIn("1")).thenReturn(false);
		String message = domainModel.resetPassword("1", "gogo", " ", " ", socketChannel);
		assertEquals(message, "not logged in");

	}

	@Test
	public void testResetPasswordUnSuccess() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.checkIfUserExists("gogo")).thenReturn(false);
		when(dataOrg.isUserLoggedIn("1")).thenReturn(true);
		String message = domainModel.resetPassword("1", "gogo", " ", " ", socketChannel);
		assertEquals(message, "unsuccessfully changed password");

	}

	@Test
	public void testUpdateUserNotLoggedIn() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.isUserLoggedIn("1")).thenReturn(false);
		String[] tokens = { "update", "alabala" };
		String message = domainModel.updateUser(tokens, "1", "gogo", socketChannel);
		assertEquals(message, "not logged in");

	}

	@Test
	public void testLogoutNotLoggedIn() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.isUserLoggedIn("1")).thenReturn(false);
		String message = domainModel.logout("1", "gogo", socketChannel);
		assertEquals(message, "not logged in");

	}
	@Test
	public void testLogoutSuccess() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.isUserLoggedIn("1")).thenReturn(true);

		HashMap<String, String> sessionsUsers = new HashMap<>();
		sessionsUsers.put("1", "gogo");
		
		HashMap<String, Session> sessions = new HashMap<>();
		Session session = new Session();
		sessions.put("1", session);

		when(dataOrg.getSessionsUsers()).thenReturn(sessionsUsers);
		when(dataOrg.getSessions()).thenReturn(sessions);

		String message = domainModel.logout("1", "gogo", socketChannel);
		assertEquals(message, "login again");

	}

	@Test
	public void testLogoutFail() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		when(dataOrg.isUserLoggedIn("1")).thenReturn(true);

		HashMap<String, String> sessionsUsers = new HashMap<>();
		sessionsUsers.put("1", "gogo");
		
		HashMap<String, Session> sessions = new HashMap<>();
		Session session = new Session();
		sessions.put("1", session);

		when(dataOrg.getSessionsUsers()).thenReturn(sessionsUsers);
		when(dataOrg.getSessions()).thenReturn(sessions);

		String message = domainModel.logout("1", "gogog", socketChannel);
		assertEquals(message, "unsuccessful logout");

	}
	
	@Test
	public void testAddAdminNotLoggedIn() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		AuditLog logger = new AuditLog();
		when(dataOrg.getLogger()).thenReturn(logger);
		when(dataOrg.isUserLoggedIn("1")).thenReturn(false);
		String message = domainModel.addAdmin("gogo", "jojo", "1", socketChannel);
		assertEquals(message, "not logged in");
		
	}

	@Test
	public void testAddAdminLoggedIn() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		AuditLog logger = new AuditLog();
		when(dataOrg.getLogger()).thenReturn(logger);
		when(dataOrg.isUserLoggedIn("1")).thenReturn(true);
		
		Set<String> admins = new HashSet<>();
		admins.add("gogo");

		HashMap<String, String> sessionsUsers = new HashMap<>();
		sessionsUsers.put("1", "gogo");
		
		when(dataOrg.getSessionsUsers()).thenReturn(sessionsUsers);


		when(dataOrg.getAdmins()).thenReturn(admins);
		String message = domainModel.addAdmin("gogo", "jojo", "1", socketChannel);
		assertEquals(message, "successfully made admin");
		
	}
	@Test
	public void testAddAdminFail() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		AuditLog logger = new AuditLog();
		when(dataOrg.getLogger()).thenReturn(logger);
		when(dataOrg.isUserLoggedIn("1")).thenReturn(true);
		
		Set<String> admins = new HashSet<>();
		admins.add("jojo");

		HashMap<String, String> sessionsUsers = new HashMap<>();
		sessionsUsers.put("1", "gogo");
		
		when(dataOrg.getSessionsUsers()).thenReturn(sessionsUsers);


		when(dataOrg.getAdmins()).thenReturn(admins);
		String message = domainModel.addAdmin("gogo", "jojo", "1", socketChannel);
		assertEquals(message, "unsuccessfully made admin");
		
	}

	@Test
	public void testRemoveAdminNotLoggedIn() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		AuditLog logger = new AuditLog();
		when(dataOrg.getLogger()).thenReturn(logger);
		when(dataOrg.isUserLoggedIn("1")).thenReturn(false);
		String message = domainModel.removeAdmin("gogo", "jojo", "1", socketChannel);
		assertEquals(message, "not logged in");
		
	}

	@Test
	public void testRemoveAdminLoggedIn() throws IOException {
		MockitoAnnotations.initMocks(this);
		SocketChannel socketChannel = SocketChannel.open();
		AuditLog logger = new AuditLog();
		when(dataOrg.getLogger()).thenReturn(logger);
		when(dataOrg.isUserLoggedIn("1")).thenReturn(true);
		
		Set<String> admins = new HashSet<>();
		admins.add("gogo");

		HashMap<String, String> sessionsUsers = new HashMap<>();
		sessionsUsers.put("1", "gogo");
		
		when(dataOrg.getSessionsUsers()).thenReturn(sessionsUsers);


		when(dataOrg.getAdmins()).thenReturn(admins);
		String message = domainModel.removeAdmin("gogo", "jojo", "1", socketChannel);
		assertEquals(message, "successfully removed admin");
		
	}

}
