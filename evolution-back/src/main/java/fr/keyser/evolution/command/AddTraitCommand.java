package fr.keyser.evolution.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class AddTraitCommand extends SpecieCardCommand {

	private final int position;

	@JsonCreator
	public AddTraitCommand(@JsonProperty("card") CardId card, @JsonProperty("specie") SpecieId specie,
			@JsonProperty("position") int position) {
		super(card, specie);
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
}
