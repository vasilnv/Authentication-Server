package bg.sofia.uni.fmi.mjt.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;

import bg.sofia.uni.fmi.mjt.auth.domain.repositories.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;


public class TestSessionRepository {
	@Mock
	private Map<String, Session> sessions;

	@InjectMocks
	private SessionRepository sessionRepository;
	
	@Before 
	public void  init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsUserLoggedIn() {
		when(sessions.containsKey("1")).thenReturn(true);

		boolean result = sessionRepository.isUserLoggedIn("1");
		
		assertTrue(result);
	}
	
	@Test
	public void testGetSessionUsername() {
		Session session = Mockito.mock(Session.class);
		when(sessions.get("1")).thenReturn(session);
		when(session.getUsername()).thenReturn("gogo");
		
		String result = sessionRepository.getSessionUsername("1");
		
		assertEquals(result, "gogo");
		
	}
	
	@Test
	public void testGetSession() {
		Session session = Mockito.mock(Session.class);
		when(sessions.get("1")).thenReturn(session);
		Session result = sessionRepository.getSession("1");
		
		assertEquals(result, session);
	}
	

}
