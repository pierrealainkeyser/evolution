package fr.keyser.evolution.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class IntelligentFeedCommand extends FeedingPhaseCommand {

	private final CardId discarded;

	@JsonCreator
	public IntelligentFeedCommand(@JsonProperty("specie") SpecieId specie,
			@JsonProperty("discarded") CardId discarded) {
		super(specie);
		this.discarded = discarded;
	}

	public CardId getDiscarded() {
		return discarded;
	}
}
