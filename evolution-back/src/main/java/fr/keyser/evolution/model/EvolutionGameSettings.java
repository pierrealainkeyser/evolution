package fr.keyser.evolution.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.security.AuthenticatedPlayer;

public class EvolutionGameSettings {

	private final List<AuthenticatedPlayer> players;

	private final boolean quickplay;

	private final List<Trait> traits;

	public EvolutionGameSettings(List<AuthenticatedPlayer> players, boolean quickplay) {
		this(players, quickplay, Collections.emptyList());
	}

	@JsonCreator
	public EvolutionGameSettings(@JsonProperty("players") List<AuthenticatedPlayer> players,
			@JsonProperty("quickplay") boolean quickplay, @JsonProperty("traits") List<Trait> traits) {
		this.players = players;
		this.quickplay = quickplay;
		this.traits = Optional.ofNullable(traits)
				.orElseGet(Collections::emptyList);
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

	@Override
	public String toString() {
		return String.format("players=%s, quickplay=%s, traits=%s", players, quickplay, traits);
	}
}
