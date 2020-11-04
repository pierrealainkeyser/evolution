package fr.keyser.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class ConnectedAuthenticatedPlayerRepository {

	private final AuthenticatedPlayerConverter authenticatedPlayerConverter;

	private final ConcurrentMap<AuthenticatedPlayer, Integer> connected = new ConcurrentHashMap<>();

	private final SimpMessageSendingOperations sendingOperations;

	public ConnectedAuthenticatedPlayerRepository(AuthenticatedPlayerConverter authenticatedPlayerConverter,
			SimpMessageSendingOperations sendingOperations) {
		this.authenticatedPlayerConverter = authenticatedPlayerConverter;
		this.sendingOperations = sendingOperations;
	}

	private void broadcast(AuthenticatedPlayer player, String type) {
		Map<String, Object> payload = Map.of("type", type, "user", player);
		sendingOperations.convertAndSend("/topic/users", payload);
	}

	private Consumer<AuthenticatedPlayer> connected(BiFunction<AuthenticatedPlayer, Integer, Integer> func) {
		return auth -> connected.compute(auth, func);
	}

	public List<AuthenticatedPlayer> getAllConnectedUsers() {
		return new ArrayList<>(connected.keySet());
	}

	protected Optional<AuthenticatedPlayer> player(AbstractSubProtocolEvent ev) {
		return Optional.ofNullable(ev.getUser()).map(authenticatedPlayerConverter::convert);
	}

	@EventListener
	public void sessionConnect(SessionConnectEvent event) {

		player(event).ifPresent(connected((auth, old) -> {
			if (old == null) {
				old = 0;
			}

			if (old == 0) {
				broadcast(auth, "connect");
			}

			// add user
			return old + 1;
		}));
	}

	@EventListener
	public void sessionDisconnect(SessionDisconnectEvent event) {

		player(event).ifPresent(connected((auth, old) -> {
			if (old != null) {
				old -= 1;

				if (old <= 0) {
					old = null;
					broadcast(auth, "disconnect");
				}
			}

			return old;
		}));
	}
}
