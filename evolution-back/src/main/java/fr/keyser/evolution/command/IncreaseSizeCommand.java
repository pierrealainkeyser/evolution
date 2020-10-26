package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class IncreaseSizeCommand extends SpecieCardCommand {

	public IncreaseSizeCommand(CardId card, SpecieId specie) {
		super(card, specie);
	}

}
