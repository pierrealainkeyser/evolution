package fr.keyser.evolution.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class IncreasePopulationCommand extends SpecieCardCommand {

	@JsonCreator
	public IncreasePopulationCommand(@JsonProperty("card") CardId card, @JsonProperty("specie") SpecieId specie) {
		super(card, specie);
	}

}
