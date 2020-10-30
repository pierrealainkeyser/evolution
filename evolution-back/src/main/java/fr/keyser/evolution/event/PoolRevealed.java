package fr.keyser.evolution.event;

import java.util.List;

import fr.keyser.evolution.core.Card;

public class PoolRevealed implements PoolEvent, DeckEvent {

	private final List<Card> cards;

	private final int delta;

	private final int food;

	public PoolRevealed(int delta, List<Card> cards, int food) {
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
