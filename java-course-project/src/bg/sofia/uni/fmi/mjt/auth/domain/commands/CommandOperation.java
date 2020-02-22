package bg.sofia.uni.fmi.mjt.auth.domain.commands;

import java.nio.channels.SocketChannel;

@FunctionalInterface
public interface CommandOperation {
	String execute();
}
