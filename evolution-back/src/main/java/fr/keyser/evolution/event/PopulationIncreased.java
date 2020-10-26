package fr.keyser.evolution.event;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class PopulationIncreased extends PopulationChanged implements DiscardedEvent {

	private final CardId discarded;

	public PopulationIncreased(SpecieId src, int to, CardId discarded) {
		super(src, to);
		this.discarded = discarded;
	}

	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	@Override
	public CardId getDiscarded() {
		return discarded;
	}

	@Override
	public String toString() {
		return String.format("PopulationIncreased [species=%s, population=%s, discarded=%s]", getSrc(), getTo(),
				discarded);
	}

}
