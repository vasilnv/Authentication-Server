package bg.sofia.uni.fmi.mjt.auth.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandAddAdmin;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandOperation;

public class TestCommandAddAdmin {
	private String message = "add-admin-user 1 gosho";
	
	@Mock
	private Domain domain;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testExecute() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		Map<SocketChannel, String> channelsByUsername = new HashMap<>();
		channelsByUsername.put(socketChannel, "gogo");
		when(domain.getChannelsByUsername()).thenReturn(channelsByUsername);
		when(domain.addAdmin("gogo", "gosho", "1", socketChannel)).thenReturn("admin added successfully");
		CommandOperation operation = new CommandAddAdmin(socketChannel, message, domain);
		CommandExecutor executor = new CommandExecutor();

		String result = executor.executeOperation(operation);
		
		assertEquals("admin added successfully", result);
	}

}
