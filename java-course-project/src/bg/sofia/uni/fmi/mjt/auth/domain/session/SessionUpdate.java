package bg.sofia.uni.fmi.mjt.auth.domain.session;

import java.time.LocalDateTime;
import java.util.Map;
import bg.sofia.uni.fmi.mjt.auth.domain.Domain;

public class SessionUpdate extends Thread{
	private static final int THREAD_SLEEP = 1000;
	private Domain domain = null;
	private static final String ERROR_MESSAGE = "Problem with Thread";
	
	private long seconds = 100;
	public SessionUpdate(Domain domain) {
		setDaemon(true);
		this.domain = domain;
	}
	
	@Override
	public void run() {
		while(true) {
			Map<String, Session> sessions = this.domain.getSessionRepository().getSessions();
			for (Map.Entry<String, Session> entry : sessions.entrySet()) {
				if(entry.getValue().getTimeOpened().plusSeconds(seconds).isBefore(LocalDateTime.now())) {
					this.domain.removeSession(entry.getValue());
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
