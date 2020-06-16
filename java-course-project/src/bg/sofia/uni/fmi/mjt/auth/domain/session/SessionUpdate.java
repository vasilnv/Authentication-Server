package bg.sofia.uni.fmi.mjt.auth.domain.session;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.SystemFacade;
import bg.sofia.uni.fmi.mjt.auth.domain.commands.Command;

public class SessionUpdate extends Thread{
	private static final int THREAD_SLEEP = 1000;
	private Domain dataOrganizer = null;
	private static final String ERROR_MESSAGE = "Problem with Thread";
	
	private long seconds = 100;
	public SessionUpdate(Domain organizer) {
		setDaemon(true);
		this.dataOrganizer = organizer;
	}
	
	@Override
	public void run() {
		while(true) {
			Map<String, Session> sessions = this.dataOrganizer.getSessionRepository().getSessions();
			for (Map.Entry<String, Session> entry : sessions.entrySet()) {
				if(entry.getValue().getTimeOpened().plusSeconds(seconds).isBefore(LocalDateTime.now())) {
					this.dataOrganizer.removeSession(entry.getValue());
				}
			}
			try {
				Thread.sleep(THREAD_SLEEP);
			} catch (InterruptedException e) {
				System.out.println(ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

}
