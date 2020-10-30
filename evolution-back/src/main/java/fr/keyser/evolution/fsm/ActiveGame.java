package fr.keyser.evolution.fsm;

import java.util.List;

import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.fsm.impl.AutomatEngine;

public class ActiveGame {
	private final GameRef ref;

	private final EvolutionGameSettings settings;

	private final AutomatEngine engine;

	public ActiveGame(GameRef ref, EvolutionGameSettings settings, AutomatEngine engine) {
		this.ref = ref;
		this.settings = settings;
		this.engine = engine;
	}

	public GameRef getRef() {
		return ref;
	}

	public AutomatEngine getEngine() {
		return engine;
	}

	public List<PlayerRef> getPlayers() {
		return ref.getPlayers();
	}

	public EvolutionGameSettings getSettings() {
		return settings;
	}

	public boolean isTerminated() {
		Object scoreboards = engine.get().getRoot().getGlobal(EvolutionGraphBuilder.SCOREBOARDS);
		return scoreboards != null;
	}
}
