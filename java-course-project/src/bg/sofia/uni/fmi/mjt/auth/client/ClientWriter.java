package bg.sofia.uni.fmi.mjt.auth.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientWriter implements Runnable {
	private static final int BUFFER_CAPACITY = 1024;
	private static final String ERROR_MESSAGE = "Problem with the channel";

	private SocketChannel socketChannel;
	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

	public ClientWriter(SocketChannel channel) {
		this.socketChannel = channel;
	}

	@Override
	public void run() {
		while (true) {
			try {
				buffer.clear();
				this.socketChannel.read(buffer);
				buffer.flip();
				String messageFromServer = new String(buffer.array(), 0, buffer.limit());
				System.out.println("The server replied <" + messageFromServer + ">");
			} catch (IOException e) {
				System.out.println(ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

}
