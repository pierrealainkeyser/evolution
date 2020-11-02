package fr.keyser.evolution.summary;

import java.util.List;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class IntelligentFeedSummary extends CostOutcome implements FeedingActionSummary {

	private final SpecieId specie;

	private final UsedTrait trait;

	public IntelligentFeedSummary(SpecieId specie, UsedTrait trait, List<Event> events, int cost) {
		super(events, cost);
		this.trait = trait;
		this.specie = specie;
	}

	@Override
	public String toString() {
		return String.format("IntelligentFeedSummary [specie=%s, cost=%s, events=%s]", specie, getCost(), getEvents());
	}

	@Override
	public SpecieId getSpecie() {
		return specie;
	}

	public UsedTrait getTrait() {
		return trait;
	}

}
