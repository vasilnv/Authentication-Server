package bg.sofia.uni.fmi.mjt.auth.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.session.SessionUpdate;
import bg.sofia.uni.fmi.mjt.auth.handler.InputHandler;

public class Server {
	private static final int HOST_PORT = 7777;
	private static final String HOST_NAME = "localhost";
	private static final int MILLISEC = 2000;

	//TODO magic strings
	public static void main(String[] args) {
		try (Selector selector = Selector.open();
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			System.out.println("opened a server successully");
			serverSocketChannel.bind(new InetSocketAddress(HOST_NAME, HOST_PORT));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			Domain dataOrganizer = Domain.getInstance();
			SystemFacade systemFacade = SystemFacade.getInstance(dataOrganizer);
			systemFacade.loadUsers();
			Thread sessionUpdater = new SessionUpdate(dataOrganizer);
			sessionUpdater.start();
			
			while (true) {
				try {
					int readyChannels = selector.select();
					if (readyChannels == 0) {
						System.out.println("Still waiting for a ready channel...");
						try {
							Thread.sleep(MILLISEC);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}

					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
					while (keyIterator.hasNext()) {
						SelectionKey key = keyIterator.next();
						if (key.isReadable()) {
							SocketChannel socketChannel = (SocketChannel) key.channel();
							System.out.println(socketChannel.isConnected());

							InputHandler inputHandler = new InputHandler(socketChannel, systemFacade);
							inputHandler.read();
						} else if (key.isAcceptable()) {
	                        ServerSocketChannel serverSocketChannelKey = (ServerSocketChannel) key.channel();
	                        SocketChannel accept = serverSocketChannelKey.accept();
	                        
	                        accept.configureBlocking(false);
	                        accept.register(selector, SelectionKey.OP_READ);
	                    }
	                    keyIterator.remove();
					}
				} catch (IOException e) {
		            System.out.println("There is a problem with the server socket");
		            e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
