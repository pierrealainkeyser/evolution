package fr.keyser.evolution.summary;

import java.util.List;

import fr.keyser.evolution.engine.Event;

public abstract class CostOutcome extends Outcome {

	private final int cost;

	public CostOutcome(List<Event> events, int cost) {
		super(events);
		this.cost = cost;
	}

	public int getCost() {
		return cost;
	}
}
