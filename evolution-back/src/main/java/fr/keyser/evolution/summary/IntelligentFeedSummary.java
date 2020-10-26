package fr.keyser.evolution.summary;

import java.util.List;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.SpecieId;

public class IntelligentFeedSummary extends CostOutcome implements Summary {

	private final SpecieId specie;

	public IntelligentFeedSummary(SpecieId specie, List<Event> events, int cost) {
		super(events, cost);
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

}
