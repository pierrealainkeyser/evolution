package fr.keyser.evolution.fsm.view;

import java.util.List;

public class AttackOutcomeView {

	private final int cost;

	private final List<String> disabled;

	private final List<RenderedEvent> events;

	public AttackOutcomeView(List<String> disabled, int cost, List<RenderedEvent> events) {
		this.disabled = disabled;
		this.cost = cost;
		this.events = events;
	}

	public int getCost() {
		return cost;
	}

	public List<String> getDisabled() {
		return disabled;
	}

	public List<RenderedEvent> getEvents() {
		return events;
	}
}
