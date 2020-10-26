package fr.keyser.evolution.event;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class SizeIncreased extends SpecieEvent implements DiscardedEvent {

	private final int to;

	private final CardId discarded;

	public SizeIncreased(SpecieId src, int to, CardId discarded) {
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

	@Override
	public CardId getDiscarded() {
		return discarded;
	}
	
	@Override
	public String toString() {
		return String.format("SizeIncreased [species=%s, size=%s, discarded=%s]", getSrc(), getTo(),
				discarded);
	}

}
