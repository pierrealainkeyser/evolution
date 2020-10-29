package fr.keyser.evolution.summary;

import java.util.List;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.SpecieId;

public class FeedSummary extends Outcome implements FeedingActionSummary {

	private final SpecieId specie;

	public FeedSummary(SpecieId specie, List<Event> events) {
		super(events);
		this.specie = specie;
	}

	@Override
	public String toString() {
		return String.format("IntelligentFeedSummary [specie=%s, events=%s]", specie, getEvents());
	}

	@Override
	public SpecieId getSpecie() {
		return specie;
	}

}
