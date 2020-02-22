package bg.sofia.uni.fmi.mjt.auth.domain.session;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
	// TODO id=null
	private String id = null;
	private int timeToLive = 20000;
	private LocalDateTime timeOpened = LocalDateTime.now();

	public Session() {
		this.id = UUID.randomUUID().toString();
	}

	public int getLivingTime() {
		return this.timeToLive;
	}
	public LocalDateTime getTimeOpened() {
		return timeOpened;
	}

	public String getId() {
		return this.id;
	}
}
