package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import fr.keyser.evolution.web.SimpMessageViewDispatcher;
import fr.keyser.security.AuthenticatedPlayerConverter;
import fr.keyser.security.ConnectedAuthenticatedPlayerRepository;

@Configuration
public class SimpConfiguration {

	@Bean
	public ConnectedAuthenticatedPlayerRepository connectedAuthenticatedPlayerRepository(
			AuthenticatedPlayerConverter authenticatedPlayerConverter, SimpMessageSendingOperations sendingOperations) {
		return new ConnectedAuthenticatedPlayerRepository(authenticatedPlayerConverter, sendingOperations);
	}

	@Bean
	public SimpMessageViewDispatcher simpMessageViewDispatcher(SimpMessageSendingOperations sendingOperations) {
		return new SimpMessageViewDispatcher(sendingOperations);
	}
}
