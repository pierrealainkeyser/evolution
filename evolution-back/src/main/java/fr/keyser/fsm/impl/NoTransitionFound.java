package fr.keyser.fsm.impl;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.State;

public class NoTransitionFound extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9062034514145126684L;

	private final State from;

	private final AutomatEvent event;

	public NoTransitionFound(State from, AutomatEvent event) {
		super("No transition found from " + from + " for " + event.getKey());
		this.from = from;
		this.event = event;
	}

	public State getFrom() {
		return from;
	}

	public AutomatEvent getEvent() {
		return event;
	}
}
