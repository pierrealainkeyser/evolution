package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class IncreasePopulationCommand extends SpecieCardCommand {

	public IncreasePopulationCommand(CardId card, SpecieId specie) {
		super(card, specie);
	}

}
