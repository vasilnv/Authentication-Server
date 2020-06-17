package bg.sofia.uni.fmi.mjt.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import bg.sofia.uni.fmi.mjt.auth.FileEditors.UserFileEditor;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class TestUserRepository {
	@Mock
	private Map<String, AuthenticatedUser> users;
	private Set<String> admins;
	private UserFileEditor userFileEditor;

	@InjectMocks
	private UserRepository userRepository;
	
	@Before 
	public void  init() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void testCheckIfUserExists() {
		when(users.containsKey("gogo")).thenReturn(true);

		boolean result = userRepository.checkIfUserExists("gogo");
		
		assertTrue(result);
	}

	@Test
	public void testCheckIfUserDoesntExists() {
		when(users.containsKey("gogo")).thenReturn(false);

		boolean result = userRepository.checkIfUserExists("gogo");
		
		assertFalse(result);
	}

	@Test
	public void testCheckIfUserIsBlockedWhenUserIsNotBlocked() {
		AuthenticatedUser user = Mockito.mock(AuthenticatedUser.class);
		when(users.get("gogo")).thenReturn(user);
		when(user.getIsBlocked()).thenReturn(false);
		
		boolean result = userRepository.checkIfUserIsBlocked("gogo");
		
		assertFalse(result);
	}

	@Test
	public void testCheckIfUserIsBlockedWhenUserIsBlocked() {
		AuthenticatedUser user = Mockito.mock(AuthenticatedUser.class);
		when(users.get("gogo")).thenReturn(user);
		when(user.getIsBlocked()).thenReturn(true);
		when(user.getBlockingTime()).thenReturn(LocalDateTime.now());
		
		boolean result = userRepository.checkIfUserIsBlocked("gogo");
		
		assertTrue(result);
	}

	@Test
	public void testCheckIfUserIsBlockedWhenUserBlockingHasExpired() {
		AuthenticatedUser user = Mockito.mock(AuthenticatedUser.class);
		when(users.get("gogo")).thenReturn(user);
		when(user.getIsBlocked()).thenReturn(true);
		when(user.getBlockingTime()).thenReturn(LocalDateTime.now().minusSeconds(100));
		
		boolean result = userRepository.checkIfUserIsBlocked("gogo");
		
		assertFalse(result);
	}

	@Test
	public void testDeleteUser() {
		String result = userRepository.deleteUser("gogo");
		
		assertEquals(result, "gogo");
	}
	
	
	@Test
	public void testUpdateUsername() {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "1234", "gogo", "gogov", "gogo@gmail.com");
		users.put("gogo", user);
		when(users.get("gogo")).thenReturn(user);
		
		String result = userRepository.updateUsername("gogo", "gogo1");
		
		assertEquals(result, "gogo1");
	}

	@Test
	public void testUpdateFirstName() {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "1234", "gogo", "gogov", "gogo@gmail.com");
		users.put("gogo", user);
		when(users.get("gogo")).thenReturn(user);
		
		String result = userRepository.updateFirstName("gogo", "gogo1");
		
		assertEquals(result, "gogo1");
	}

	@Test
	public void testUpdateLastName() {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "1234", "gogo", "gogov", "gogo@gmail.com");
		users.put("gogo", user);
		when(users.get("gogo")).thenReturn(user);
		
		String result = userRepository.updateLastName("gogo", "gogo1");
		
		assertEquals(result, "gogo1");
	}
	
	@Test
	public void testUpdateEmail() {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "1234", "gogo", "gogov", "gogo@gmail.com");
		users.put("gogo", user);
		when(users.get("gogo")).thenReturn(user);
		
		String result = userRepository.updateEmail("gogo", "gogo1");
		
		assertEquals(result, "gogo1");
	}

	@Test
	public void testUpdatePassword() {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "1234", "gogo", "gogov", "gogo@gmail.com");
		users.put("gogo", user);
		when(users.get("gogo")).thenReturn(user);
		
		String result = userRepository.updatePassword("gogo", "12345");
		
		assertEquals(result, "12345");
	}

	@Test
	public void testGetPassword() {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "1234", "gogo", "gogov", "gogo@gmail.com");
		users.put("gogo", user);
		when(users.get("gogo")).thenReturn(user);
		
		String result = userRepository.getUserPassword("gogo");
		
		assertEquals(result, "1234");
	}

	@Test
	public void testCheckIfUserIsAdmin() {
		AuthenticatedUser user = new AuthenticatedUser("gogo", "1234", "gogo", "gogov", "gogo@gmail.com");
		users.put("gogo", user);
		when(users.get("gogo")).thenReturn(user);
		
		userRepository.load();
		boolean result = userRepository.checkIfUserIsAdmin("gogo1");
		
		assertTrue(result);
	}



	
/*	@Test
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


*/
}
