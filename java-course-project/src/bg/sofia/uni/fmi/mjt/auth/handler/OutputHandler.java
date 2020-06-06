package bg.sofia.uni.fmi.mjt.auth.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class OutputHandler {
	private static final String ERROR_MESSAGE = "Cannot write in the channel";
	private SocketChannel socketChannel = null;

	public OutputHandler(SocketChannel sc) {
		this.socketChannel = sc;
		System.out.println("outputHandler created");
	}
	public void write(String message) {
		ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
		try {
			socketChannel.write(buffer);
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
