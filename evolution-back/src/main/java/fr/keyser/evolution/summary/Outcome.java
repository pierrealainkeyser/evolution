package fr.keyser.evolution.summary;

import java.util.List;

import fr.keyser.evolution.engine.Event;

public abstract class Outcome {
	
	private final List<Event> events;

	public Outcome(List<Event> events) {
		this.events = events;
	}

	public List<Event> getEvents() {
		return events;
	}
}