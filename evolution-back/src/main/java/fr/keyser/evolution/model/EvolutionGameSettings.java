package fr.keyser.evolution.model;

import java.util.Collections;
import java.util.List;

import fr.keyser.evolution.fsm.AuthenticatedPlayer;

public class EvolutionGameSettings {

	private final List<AuthenticatedPlayer> players;

	private final boolean quickplay;

	private final List<Trait> traits;

	public EvolutionGameSettings(List<AuthenticatedPlayer> players, boolean quickplay) {
		this(players, quickplay, Collections.emptyList());
	}

	public EvolutionGameSettings(List<AuthenticatedPlayer> players, boolean quickplay, List<Trait> traits) {
		this.players = players;
		this.quickplay = quickplay;
		this.traits = traits;
	}

	public List<AuthenticatedPlayer> getPlayers() {
		return players;
	}

	public int getPlayersCount() {
		return players.size();
	}

	public EvolutionGameParameters getParameters() {
		return new EvolutionGameParameters(getPlayersCount(), quickplay);
	}

	public boolean isQuickplay() {
		return quickplay;
	}

	public boolean isCustomTraits() {
		return !traits.isEmpty();
	}

	public List<Trait> getTraits() {
		return traits;
	}
}
