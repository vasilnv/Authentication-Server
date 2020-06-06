package bg.sofia.uni.fmi.mjt.auth.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
	private static final int SERVER_PORT = 7777;
	private static final String SERVER_HOST = "localhost";
	private static final String ERROR_MESSAGE = "Problem with the channel";
	
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);

	public static void main(String[] args) {

		try (SocketChannel socketChannel = SocketChannel.open(); 
				Scanner scanner = new Scanner(System.in)) {
			socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
			new Thread(new ClientWriter(socketChannel)).start();

			System.out.println("Connected to the server.");
			System.out.println("commands you can use: \n"
					+ "register <username> <password> <firstName> <lastName> <email> \n"
					+ "login <username> <password> \n" + "login <sessionId> \n"
					+ "update-user <session-id> <newUsername> <newFirstName> <newLastName> <email> "
					+ "(after sesionId its optional) \n"
					+ "reset-password <session-id> <username> <oldPassword> <newPassword> \n"
					+ "logout <sessionId> \n"
					+ "add-admin-user <sessionId> <username> \n"
					+ "remove-admin-user <sessionId> <username> \n"
					+ "delete-user <sessionId> <username>");

			while (true) {
				String message = scanner.nextLine();

				buffer.clear();
				buffer.put(message.getBytes());
				buffer.flip();

				socketChannel.write(buffer);
			}

		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
