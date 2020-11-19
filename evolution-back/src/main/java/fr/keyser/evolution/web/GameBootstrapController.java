package fr.keyser.evolution.web;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.keyser.evolution.fsm.ActiveGame;
import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.GameRef;
import fr.keyser.evolution.fsm.GameResolver;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.evolution.overview.GameOverview;
import fr.keyser.evolution.overview.GameOverviewRepository;
import fr.keyser.security.AuthenticatedPlayer;
import fr.keyser.security.AuthenticatedPlayerConverter;

@RestController
@RequestMapping("/game")
public class GameBootstrapController {

	private final GameResolver resolver;

	private final GameBuilder gameBuilder;

	private final GameOverviewRepository gameOverviewRepository;

	private final AuthenticatedPlayerConverter authenticatedPlayerConverter;

	public GameBootstrapController(GameResolver resolver, GameBuilder gameBuilder,
			GameOverviewRepository gameOverviewRepository, AuthenticatedPlayerConverter authenticatedPlayerConverter) {
		this.resolver = resolver;
		this.gameBuilder = gameBuilder;
		this.gameOverviewRepository = gameOverviewRepository;
		this.authenticatedPlayerConverter = authenticatedPlayerConverter;
	}

	@PostMapping("/bootstrap")
	public GameOverview bootstrap(@RequestBody EvolutionGameSettings bootstrap, Principal principal) {
		ActiveGame created = gameBuilder.create(bootstrap);

		AuthenticatedPlayer authen = authenticatedPlayerConverter.convert(principal);

		resolver.addGame(created, authen);

		GameRef ref = created.getRef();

		List<GameOverview> overviews = gameOverviewRepository.overview(ref);

		return overviews.stream().filter(g -> g.getUser().equals(authen.getName())).findFirst().get();

	}

	@DeleteMapping("/{gameId}")
	public void remove(@PathVariable String gameId, Principal principal) {
		AuthenticatedPlayer authen = authenticatedPlayerConverter.convert(principal);
	}

}
