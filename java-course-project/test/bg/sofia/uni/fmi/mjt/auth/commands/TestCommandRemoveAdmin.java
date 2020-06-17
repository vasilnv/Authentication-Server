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
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandOperation;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandRemoveAdmin;

public class TestCommandRemoveAdmin {
	private String message = "remove-admin-user 1 gosho";
	
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
		when(domain.removeAdmin("gogo", "gosho", "1", socketChannel)).thenReturn("admin removed successfully");
		CommandOperation operation = new CommandRemoveAdmin(socketChannel, message, domain);
		CommandExecutor executor = new CommandExecutor();

		String result = executor.executeOperation(operation);
		
		assertEquals("admin removed successfully", result);
	}

}
