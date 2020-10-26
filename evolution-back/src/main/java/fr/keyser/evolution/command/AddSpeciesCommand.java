package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpeciePosition;

public class AddSpeciesCommand extends CardCommand implements PlayCardsPhaseCommand {

	private final SpeciePosition position;

	public AddSpeciesCommand(CardId card, SpeciePosition position) {
		super(card);
		this.position = position;
	}

	public SpeciePosition getPosition() {
		return position;
	}
}
