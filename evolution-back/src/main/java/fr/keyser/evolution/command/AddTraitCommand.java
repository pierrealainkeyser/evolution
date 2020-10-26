package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class AddTraitCommand extends SpecieCardCommand {

	private final int position;

	public AddTraitCommand(CardId card, SpecieId specie, int position) {
		super(card, specie);
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
}
