package fr.keyser.evolution.web;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.simp.annotation.SubscribeMapping;

import fr.keyser.evolution.overview.GameOverview;
import fr.keyser.evolution.overview.GameOverviewRepository;
import fr.keyser.security.AuthenticatedPlayer;
import fr.keyser.security.AuthenticatedPlayerConverter;

public class GameOverviewController {

	private final GameOverviewRepository gameOverviewRepository;

	private final AuthenticatedPlayerConverter authenticatedPlayerConverter;

	public GameOverviewController(GameOverviewRepository gameOverviewRepository,
			AuthenticatedPlayerConverter authenticatedPlayerConverter) {
		this.gameOverviewRepository = gameOverviewRepository;
		this.authenticatedPlayerConverter = authenticatedPlayerConverter;
	}

	@SubscribeMapping("/my-games")
	public List<GameOverview> myGames(Principal principal) {
		AuthenticatedPlayer player = authenticatedPlayerConverter.convert(principal);
		return gameOverviewRepository.myGames(player);
	}
}
