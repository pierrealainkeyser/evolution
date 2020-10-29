package fr.keyser.evolution.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpeciePosition;

public class AddSpeciesCommand extends CardCommand implements PlayCardsPhaseCommand {

	private final SpeciePosition position;

	@JsonCreator
	public AddSpeciesCommand(@JsonProperty("card") CardId card, @JsonProperty("position") SpeciePosition position) {
		super(card);
		this.position = position;
	}

	public SpeciePosition getPosition() {
		return position;
	}
}
