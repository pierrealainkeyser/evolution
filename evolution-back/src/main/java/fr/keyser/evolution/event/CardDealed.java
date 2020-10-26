package fr.keyser.evolution.event;

import java.util.List;

import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardId;

public class CardDealed implements PlayerEvent, DeckEvent {

	private final int player;

	private final boolean shuffle;

	private final List<Card> cards;

	private final List<CardId> shuffledCards;

	public CardDealed(int player, List<Card> cards, boolean shuffle, List<CardId> shuffledCards) {
		this.player = player;
		this.shuffle = shuffle;
		this.cards = cards;
		this.shuffledCards = shuffledCards;
	}

	@Override
	public int getPlayer() {
		return player;
	}

	public List<Card> getCards() {
		return cards;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public List<CardId> getShuffledCards() {
		return shuffledCards;
	}

	@Override
	public String toString() {
		return String.format("CardDealed [player=%s, cards=%s, shuffle=%s, shuffledCards=%s]", player, cards, shuffle,
				shuffledCards);
	}
}
