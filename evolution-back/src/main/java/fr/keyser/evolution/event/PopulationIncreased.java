package fr.keyser.evolution.event;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class PopulationIncreased extends PopulationChanged implements DiscardedEvent {

	private final Card discarded;

	public PopulationIncreased(SpecieId src, int to, Card discarded) {
		super(src, to);
		this.discarded = discarded;
	}

	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	public Card getCard() {
		return discarded;
	}

	@Override
	public CardId getDiscarded() {
		return discarded.getId();
	}

	@Override
	public String toString() {
		return String.format("PopulationIncreased [species=%s, population=%s, discarded=%s]", getSrc(), getTo(),
				discarded);
	}

}
