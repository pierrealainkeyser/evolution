package fr.keyser.evolution.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.SpecieId;

public class FeedCommand extends FeedingPhaseCommand {

	@JsonCreator
	public FeedCommand(@JsonProperty("specie") SpecieId specie) {
		super(specie);
	}

}
