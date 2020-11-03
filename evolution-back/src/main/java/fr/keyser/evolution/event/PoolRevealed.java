package fr.keyser.evolution.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Card;

public class PoolRevealed implements PoolEvent, DeckEvent {

	private final List<Card> cards;

	private final int delta;

	private final int food;

	@JsonCreator
	public PoolRevealed(@JsonProperty("delta") int delta,@JsonProperty("cards") List<Card> cards,@JsonProperty("food") int food) {
		this.delta = delta;
		this.cards = cards;
		this.food = food;
	}

	public List<Card> getCards() {
		return cards;
	}

	public int getDelta() {
		return delta;
	}

	public int getFood() {
		return food;
	}

	@Override
	public String toString() {
		return String.format("PoolRevealed [cards=%s, delta=%s, food=%s]", cards, delta, food);
	}
}
