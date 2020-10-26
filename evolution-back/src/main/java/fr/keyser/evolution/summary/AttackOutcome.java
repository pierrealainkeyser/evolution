package fr.keyser.evolution.summary;

import java.util.List;

import fr.keyser.evolution.engine.Event;

public class AttackOutcome extends CostOutcome {

	private final List<String> disabled;

	public AttackOutcome(List<Event> events, int cost, List<String> disabled) {
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
