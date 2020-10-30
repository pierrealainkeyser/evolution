package fr.keyser.evolution.event;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class SizeIncreased extends SpecieEvent implements DiscardedEvent {

	private final int to;

	private final Card discarded;

	public SizeIncreased(SpecieId src, int to, Card discarded) {
		super(src);
		this.to = to;
		this.discarded = discarded;
	}

	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	public int getTo() {
		return to;
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
		return String.format("SizeIncreased [species=%s, size=%s, discarded=%s]", getSrc(), getTo(),
				discarded);
	}

}
