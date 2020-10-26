package fr.keyser.evolution.event;

import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class TraitAdded extends SpecieEvent implements DiscardedEvent {

	private final Card card;
	private final int index;
	private final Card replaced;

	public TraitAdded(SpecieId src, Card card, int index, Card replaced) {
		super(src);
		this.card = card;
		this.index = index;
		this.replaced = replaced;
	}

	public Card getCard() {
		return card;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return String.format("TraitAdded [specie=%s, card=%s, index=%s, replaced=%s]", getSrc(), card, index, replaced);
	}

	@Override
	public CardId getDiscarded() {
		return card.getId();
	}

	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	public Card getReplaced() {
		return replaced;
	}

}
