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
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandLogIn;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandOperation;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class TestCommandLogin {
	private String messageLoginByUsernameAndPassword = "login gogo 1234";
	private String messageLoginBySession = "login 1";
	private String messageWrongCommand = "login";
	
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
		String password = PasswordEncoder.encodePass("1234", "javaIsCool"); 
		when(domain.logInByNameAndPassword("gogo", password, socketChannel)).thenReturn("logged in");
		CommandOperation operation = new CommandLogIn(socketChannel, messageLoginByUsernameAndPassword , domain);
		CommandExecutor executor = new CommandExecutor();

		String result = executor.executeOperation(operation);
		
		assertEquals("logged in", result);
	}

	@Test
	public void testExecuteLoginBySession() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		when(domain.logInBySession("1", socketChannel)).thenReturn("logged in");
		CommandOperation operation = new CommandLogIn(socketChannel, messageLoginBySession , domain);
		CommandExecutor executor = new CommandExecutor();

		String result = executor.executeOperation(operation);
		
		assertEquals("logged in", result);
	}

	@Test
	public void testExecuteLoginWrongNumberOfParameters() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		CommandOperation operation = new CommandLogIn(socketChannel, messageWrongCommand , domain);
		CommandExecutor executor = new CommandExecutor();

		String result = executor.executeOperation(operation);
		
		assertEquals("wrong command", result);
	}

	
}
