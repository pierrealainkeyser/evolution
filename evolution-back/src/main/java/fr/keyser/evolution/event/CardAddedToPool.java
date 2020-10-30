package fr.keyser.evolution.event;

import fr.keyser.evolution.core.Card;

public class CardAddedToPool implements PlayerEvent, PoolEvent {

	private final Card card;

	private final int player;

	public CardAddedToPool(int player, Card card) {
		this.player = player;
		this.card = card;
	}

	@Override
	public int getPlayer() {
		return player;
	}

	public Card getCard() {
		return card;
	}

	@Override
	public String toString() {
		return String.format("CardAddedToPool [card=%s, player=%s]", card, player);
	}

}
