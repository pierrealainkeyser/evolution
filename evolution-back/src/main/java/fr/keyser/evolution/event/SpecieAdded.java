package fr.keyser.evolution.event;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;

public class SpecieAdded extends SpecieEvent implements DiscardedEvent {

	private final CardId discarded;

	private final SpeciePosition position;

	public SpecieAdded(SpecieId src, SpeciePosition position, CardId discarded) {
		super(src);
		this.discarded = discarded;
		this.position = position;
	}

	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	@Override
	public CardId getDiscarded() {
		return discarded;
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
