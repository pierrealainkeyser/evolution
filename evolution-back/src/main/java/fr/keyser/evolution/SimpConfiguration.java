package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import fr.keyser.evolution.web.SimpMessageViewDispatcher;

@Configuration
public class SimpConfiguration {

	@Bean
	public SimpMessageViewDispatcher simpMessageViewDispatcher(SimpMessageSendingOperations sendingOperations) {
		return new SimpMessageViewDispatcher(sendingOperations);
	}
}
