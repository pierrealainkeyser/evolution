package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import fr.keyser.evolution.fsm.CachedGameResolver;
import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.overview.GameOverviewRepository;
import fr.keyser.evolution.overview.OverviewDispatcher;
import fr.keyser.evolution.overview.OverviewGameResolver;
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

	@Bean
	@Primary
	public OverviewGameResolver overviewGameResolver(CachedGameResolver delegated, GameBuilder gameBuilder,
			GameOverviewRepository repository, OverviewDispatcher dispatcher) {
		return new OverviewGameResolver(delegated, gameBuilder, repository, dispatcher);

	}
}
