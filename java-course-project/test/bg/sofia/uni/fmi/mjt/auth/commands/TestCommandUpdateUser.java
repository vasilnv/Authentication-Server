package bg.sofia.uni.fmi.mjt.auth.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandOperation;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandUpdateUser;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class TestCommandUpdateUser {
	private String message = "update-user 1 new-username gogo new-firstname gogo new-lastname gogov new-email gogov@gmail.com";
	
	@Mock
	private Domain domain;
	@Mock
	private AuthenticatedUser user;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testExecuteLoginByUsernameAndPassword() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		when(domain.updateUser(anyString(), anyString(), anyString(), anyString())).thenReturn("updated successfully");
		CommandOperation operation = new CommandUpdateUser(socketChannel, message, domain);
		CommandExecutor executor = new CommandExecutor();

		String result = executor.executeOperation(operation);
		
		assertEquals("updated successfully", result);
	}
}
