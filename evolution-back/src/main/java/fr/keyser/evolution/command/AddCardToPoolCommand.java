package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;

public class AddCardToPoolCommand extends CardCommand {

	public AddCardToPoolCommand(CardId card) {
		super(card);
	}

}
