package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Card;

public class CardAddedToPool implements PlayerEvent, PoolEvent {

	private final Card card;

	private final int player;

	@JsonCreator
	public CardAddedToPool(@JsonProperty("player") int player, @JsonProperty("card") Card card) {
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
