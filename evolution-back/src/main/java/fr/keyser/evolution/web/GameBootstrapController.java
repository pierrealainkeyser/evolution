package fr.keyser.evolution.web;

import java.security.Principal;

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
import fr.keyser.evolution.fsm.ResolvedRef;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.security.AuthenticatedPlayer;
import fr.keyser.security.AuthenticatedPlayerConverter;

@RestController
@RequestMapping("/game")
public class GameBootstrapController {

	private final GameResolver resolver;

	private final GameBuilder gameBuilder;

	private final AuthenticatedPlayerConverter authenticatedPlayerConverter;

	public GameBootstrapController(GameResolver resolver, GameBuilder gameBuilder,
			AuthenticatedPlayerConverter authenticatedPlayerConverter) {
		this.resolver = resolver;
		this.gameBuilder = gameBuilder;
		this.authenticatedPlayerConverter = authenticatedPlayerConverter;
	}

	@PostMapping("/bootstrap")
	public ResolvedRef bootstrap(@RequestBody EvolutionGameSettings bootstrap, Principal principal) {
		ActiveGame created = gameBuilder.create(bootstrap);

		AuthenticatedPlayer authen = authenticatedPlayerConverter.convert(principal);

		resolver.addGame(created, authen);

		GameRef ref = created.getRef();
		return new ResolvedRef(
				ref.getPlayers().stream().filter(p -> p.getUser().equals(authen)).findFirst().get(),
				ref);

	}

	@DeleteMapping("/{gameId}")
	public void remove(@PathVariable String gameId, Principal principal) {

	}

}
