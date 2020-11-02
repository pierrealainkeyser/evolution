package fr.keyser.evolution.event;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;

public class SpecieAdded extends SpecieEvent implements DiscardedEvent {

	private final Card discarded;

	private final SpeciePosition position;

	public SpecieAdded(SpecieId src, SpeciePosition position, Card discarded) {
		super(src);
		this.discarded = discarded;
		this.position = position;
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
		if (discarded == null)
			return null;
		return discarded.getId();
	}

	public SpeciePosition getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return String.format("SpecieAdded [specie=%s, discarded=%s, position=%s]", getSrc(), discarded,
				position);
	}
}
