package fr.keyser.evolution.summary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.engine.Event;

public class AttackOutcome extends CostOutcome {

	private final List<String> disabled;

	@JsonCreator
	public AttackOutcome(@JsonProperty("events") List<Event> events, @JsonProperty("cost") int cost,
			@JsonProperty("disabled") List<String> disabled) {
		super(events, cost);
		this.disabled = disabled;
	}

	public List<String> getDisabled() {
		return disabled;
	}

	@Override
	public String toString() {
		return String.format("AttackOutcome [cost=%s, disabled=%s, events=%s]", getCost(), disabled, getEvents());
	}

}
