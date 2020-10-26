package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public abstract class SpecieCardCommand extends CardCommand implements SpecieCommand, PlayCardsPhaseCommand {

	private final SpecieId specie;

	public SpecieCardCommand(CardId card, SpecieId specie) {
		super(card);
		this.specie = specie;
	}

	public SpecieId getSpecie() {
		return specie;
	}
}
