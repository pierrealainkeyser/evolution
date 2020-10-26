package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;

public abstract class CardCommand implements Command {

	private final CardId card;

	public CardCommand(CardId card) {
		this.card = card;
	}

	public CardId getCard() {
		return card;
	}
}
