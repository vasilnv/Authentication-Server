package bg.sofia.uni.fmi.mjt.auth;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import bg.sofia.uni.fmi.mjt.auth.domain.UserUpdater;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.repositories.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class TestUserUpdater {
	@Mock
	private UserRepository userRepository;
	@Mock
	private SessionRepository sessionRepository;

	@InjectMocks
	private UserUpdater userUpdater;
	
	@Before 
	public void  init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testChangeUsername() throws IOException {
		String sessionId = "1";
		Session session = new Session();
		AuthenticatedUser user = Mockito.mock(AuthenticatedUser.class, Mockito.RETURNS_DEEP_STUBS);
		when(userRepository.getUser("gogo1")).thenReturn(user);
		when(user.getSessionID()).thenReturn(sessionId);
		when(sessionRepository.getSession(sessionId)).thenReturn(session);
		
		String name = userUpdater.changeUsername("gogo", "gogo1");
	
		assertEquals(name, "gogo1");
	}
	
	@Test
	public void testChangeFirstName() {
		String result = userUpdater.changeFirstName("gogo", "gogo1");
	
		assertEquals(result, "gogo1");
	}
	
	@Test
	public void testChangeLastName() {
		String result = userUpdater.changeLastName("gogo", "gogo1");
	
		assertEquals(result, "gogo1");
	}
	@Test
	public void testChangeEmail() {
		String result = userUpdater.changeEmail("gogo", "gogo1");
	
		assertEquals(result, "gogo1");
	}
	
}
