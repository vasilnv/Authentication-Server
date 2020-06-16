package bg.sofia.uni.fmi.mjt.auth.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.CommandDistributor;
import bg.sofia.uni.fmi.mjt.auth.domain.session.SessionUpdate;

public class InputHandler {
	private static final int BUFFER_CAPACITY = 1024;
	private static final String ERROR_MESSAGE = "Problem with the Socket Channel";

	private SocketChannel socketChannel = null;
	private SystemFacade systemFacade = null;

	public InputHandler(SocketChannel socketChannel, SystemFacade facadeInstance) {
		this.socketChannel = socketChannel;
		System.out.println("inputHandler created");
		this.systemFacade = facadeInstance;
	}

	public void read() {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
			StringBuffer messageBuffer = new StringBuffer();
			while (socketChannel.isConnected() && socketChannel.read(buffer) > 0) {
				buffer.flip();
				messageBuffer.append(new String(buffer.array()), 0, buffer.limit());
				buffer.clear();
			}
			String messageToServer = messageBuffer.toString();
			CommandDistributor distributor = new CommandDistributor();
			distributor.messageReceive(messageToServer, this.socketChannel, systemFacade);
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
