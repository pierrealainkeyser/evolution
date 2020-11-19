package fr.keyser.evolution.overview;

import java.util.List;

import fr.keyser.evolution.fsm.ActiveGame;
import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.GameRef;
import fr.keyser.evolution.fsm.GameResolver;
import fr.keyser.evolution.fsm.ResolvedRef;
import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.security.AuthenticatedPlayer;

public class OverviewGameResolver implements GameResolver {

	private final GameOverviewRepository repository;

	private final GameResolver delegated;

	private final GameBuilder gameBuilder;

	private final OverviewDispatcher dispatcher;

	public OverviewGameResolver(GameResolver delegated, GameBuilder gameBuilder, GameOverviewRepository repository,
			OverviewDispatcher dispatcher) {
		this.delegated = delegated;
		this.gameBuilder = gameBuilder;
		this.repository = repository;
		this.dispatcher = dispatcher;
	}

	@Override
	public ResolvedRef findByUuid(String uuid) {
		return delegated.findByUuid(uuid);
	}

	@Override
	public AutomatEngine getEngine(GameRef ref) {
		return delegated.getEngine(ref);
	}

	@Override
	public void updateEngine(GameRef ref, AutomatEngine engine) {
		delegated.updateEngine(ref, engine);

		if (gameBuilder.isTerminated(engine)) {

			List<GameOverview> overview = repository.overview(ref);
			dispatcher.dispatch("terminated", overview);
		}

	}

	@Override
	public void addGame(ActiveGame active, AuthenticatedPlayer owner) {
		delegated.addGame(active, owner);

		List<GameOverview> overview = repository.overview(active.getRef());
		dispatcher.dispatch("started", overview);
	}

}
