package bg.sofia.uni.fmi.mjt.auth.domain.repositories;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bg.sofia.uni.fmi.mjt.auth.domain.session.Session;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class SessionRepository {
	private Map<String, Session> sessions = new ConcurrentHashMap<>();

	public Map<String, Session> getSessions() {
		return sessions;
	}
	
	public boolean isUserLoggedIn(String sessionId) {
		return (sessions.containsKey(sessionId));
	}

	public void addSession(Session session) {
		sessions.put(session.getId(), session);
	}

	public void mapSessionUser(Session session, AuthenticatedUser newUser) {
		session.setUsername(newUser.getUsername());
		newUser.setSession(session.getId());
	}
	
	public void removeSession(Session session) {
		sessions.remove(session.getId());
	}
	
	public String getSessionUsername(String sessionId) {
		return sessions.get(sessionId).getUsername();
	}
	public void setSessionUsername(String sessionId, String username) {
		sessions.get(sessionId).setUsername(username);
	}
	
	public Session getSession(String sessionId) {
		return this.sessions.get(sessionId);
	}

}
