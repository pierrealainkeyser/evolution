package fr.keyser.evolution.summary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class IntelligentFeedSummary extends CostOutcome implements FeedingActionSummary {

	private final SpecieId specie;

	private final CardId card;

	private final UsedTrait trait;

	private final boolean optional;

	@JsonCreator
	public IntelligentFeedSummary(@JsonProperty("optional") boolean optional, @JsonProperty("specie") SpecieId specie,
			@JsonProperty("card") CardId card,
			@JsonProperty("trait") UsedTrait trait, @JsonProperty("events") List<Event> events,
			@JsonProperty("cost") int cost) {
		super(events, cost);
		this.optional = optional;
		this.trait = trait;
		this.card = card;
		this.specie = specie;
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public String toString() {
		return String.format("IntelligentFeedSummary [specie=%s, cost=%s, card=%s events=%s]", specie, getCost(), card,
				getEvents());
	}

	@Override
	public SpecieId getSpecie() {
		return specie;
	}

	public UsedTrait getTrait() {
		return trait;
	}

	public CardId getCard() {
		return card;
	}

}
