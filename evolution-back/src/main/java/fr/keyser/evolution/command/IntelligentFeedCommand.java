package fr.keyser.evolution.command;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class IntelligentFeedCommand extends FeedingPhaseCommand {

	private final CardId discarded;

	public IntelligentFeedCommand(SpecieId specie, CardId discarded) {
		super(specie);
		this.discarded = discarded;
	}

	public CardId getDiscarded() {
		return discarded;
	}
}
