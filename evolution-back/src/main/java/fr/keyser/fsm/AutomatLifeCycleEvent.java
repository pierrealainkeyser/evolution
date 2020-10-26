package fr.keyser.fsm;

import java.util.function.Supplier;

public enum AutomatLifeCycleEvent implements Supplier<AutomatEvent> {
	START, CHILDS_STARTED, //

	JOIN, CHILD_JOIN, // RendezVous

	CHOICE; // choice

	private final AutomatEvent event;

	private AutomatLifeCycleEvent() {
		event = new AutomatEvent(this, null);
	}

	@Override
	public AutomatEvent get() {
		return event;
	}

	public AutomatEvent event(Object payload) {
		return new AutomatEvent(this, payload);
	}
}
