package fr.keyser.evolution.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.CardId;

public class AddCardToPoolCommand extends CardCommand {

	@JsonCreator
	public AddCardToPoolCommand(@JsonProperty("card") CardId card) {
		super(card);
	}

}
