package bg.sofia.uni.fmi.mjt.auth.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandOperation;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandResetPassword;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class TestCommandResetPassword {
	private String message = "reset-password 1 gogo 1234 12345";
	
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
		String oldPassword = PasswordEncoder.encodePass("1234", "javaIsCool"); 
		String newPassword = PasswordEncoder.encodePass("12345", "javaIsCool"); 
		when(domain.resetPassword("1", "gogo", oldPassword, newPassword, socketChannel)).thenReturn("success");
		CommandOperation operation = new CommandResetPassword(socketChannel, message, domain);
		CommandExecutor executor = new CommandExecutor();

		String result = executor.executeOperation(operation);
		
		assertEquals("success", result);
	}

}
