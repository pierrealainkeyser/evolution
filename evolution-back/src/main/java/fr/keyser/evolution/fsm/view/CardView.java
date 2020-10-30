package fr.keyser.evolution.fsm.view;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;

public class CardView {

	public static final CardView EMPTY = new CardView(null);

	private final Card card;

	public CardView(Card card) {
		this.card = card;
	}

	public CardId getId() {
		if (card == null)
			return null;

		return card.getId();
	}

	public String getTrait() {
		if (card == null)
			return "?";

		return card.getMeta().getTrait().name();
	}

	public int getFood() {
		if (card == null)
			return -1;

		return card.getMeta().getFood();
	}

}
