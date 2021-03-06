package fr.keyser.evolution.web;

import java.util.Map;

import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import fr.keyser.security.ConnectedAuthenticatedPlayerRepository;

@Controller
public class ConnectedPlayerController {

	private final ConnectedAuthenticatedPlayerRepository repository;

	public ConnectedPlayerController(ConnectedAuthenticatedPlayerRepository userService) {
		this.repository = userService;
	}

	@SubscribeMapping("/users")
	public Map<String, Object> connectedUsers() {
		return Map.of("type", "all", "users", repository.getAllConnectedUsers());
	}
}
