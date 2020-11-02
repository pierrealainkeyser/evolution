package fr.keyser.evolution.model;

import java.util.Objects;

public class EvolutionGameParameters {

	private final int playersCount;

	private final boolean quickplay;

	public EvolutionGameParameters(int players, boolean quickplay) {
		this.playersCount = players;
		this.quickplay = quickplay;
	}

	public int getPlayersCount() {
		return playersCount;
	}

	public boolean isQuickplay() {
		return quickplay;
	}

	@Override
	public int hashCode() {
		return Objects.hash(playersCount, quickplay);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EvolutionGameParameters))
			return false;
		EvolutionGameParameters other = (EvolutionGameParameters) obj;
		return playersCount == other.playersCount && quickplay == other.quickplay;
	}
}
