package bg.sofia.uni.fmi.mjt.auth;

import org.junit.Test;

import bg.sofia.uni.fmi.mjt.auth.domain.DataOrganizer;
import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TestDataOrganizer {
	
	@Test
	public void testIfAddsSessionsCorrectly() {
		Session mockSession = mock(Session.class);
		when(mockSession.getId()).thenReturn("1");
		DataOrganizer data = new DataOrganizer();
		data.addSession(mockSession);
		assertTrue(data.getSessions().containsKey(mockSession.getId()));
	}

	@Test
	public void testIfAddsUsersSessionsCorrectly() {
		Session session = mock(Session.class);
		String username = "gogo";
		when(session.getId()).thenReturn("1");
		DataOrganizer data = new DataOrganizer();
		data.addUserSession(username, session);
		assertTrue(data.getUsersSessions().containsKey(username));
	}

	@Test
	public void testIfAddsSessionsUsersCorrectly() {
		Session session = mock(Session.class);
		String username = "gogo";
		when(session.getId()).thenReturn("1");
		DataOrganizer data = new DataOrganizer();
		data.addSessionUser(username, session);
		assertTrue(data.getSessionsUsers().containsKey(session.getId()));
	}



}
