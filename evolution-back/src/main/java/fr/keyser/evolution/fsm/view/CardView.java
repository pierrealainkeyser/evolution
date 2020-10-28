package fr.keyser.evolution.fsm.view;

import fr.keyser.evolution.model.Card;

public class CardView {

	private final Card card;

	public CardView(Card card) {
		this.card = card;
	}

	public String getId() {
		return card.getId().toString();
	}

	public String getTrait() {
		return card.getMeta().getTrait().name();
	}

	public int getFood() {
		return card.getMeta().getFood();
	}

}
