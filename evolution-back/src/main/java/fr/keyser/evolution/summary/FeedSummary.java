package fr.keyser.evolution.summary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.SpecieId;

public class FeedSummary extends Outcome implements FeedingActionSummary {

	private final SpecieId specie;

	@JsonCreator
	public FeedSummary(@JsonProperty("specie") SpecieId specie, @JsonProperty("events") List<Event> events) {
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
