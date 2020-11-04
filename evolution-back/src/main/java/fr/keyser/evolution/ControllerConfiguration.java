package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.keyser.evolution.fsm.BridgeService;
import fr.keyser.evolution.overview.GameOverviewRepository;
import fr.keyser.evolution.web.ConnectedPlayerController;
import fr.keyser.evolution.web.EvolutionGameController;
import fr.keyser.evolution.web.GameOverviewController;
import fr.keyser.security.AuthenticatedPlayerConverter;
import fr.keyser.security.ConnectedAuthenticatedPlayerRepository;

@Configuration
public class ControllerConfiguration {

	@Bean
	public ConnectedPlayerController connectedPlayerController(ConnectedAuthenticatedPlayerRepository repository) {
		return new ConnectedPlayerController(repository);
	}

	@Bean
	public EvolutionGameController evolutionGameController(BridgeService bridgeService) {
		return new EvolutionGameController(bridgeService);
	}

	@Bean
	public GameOverviewController gameOverviewController(GameOverviewRepository gameOverviewRepository,
			AuthenticatedPlayerConverter authenticatedPlayerConverter) {
		return new GameOverviewController(gameOverviewRepository, authenticatedPlayerConverter);
	}
}
