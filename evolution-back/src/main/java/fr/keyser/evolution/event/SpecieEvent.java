package fr.keyser.evolution.event;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.SpecieId;

public abstract class SpecieEvent implements Event {
	private final SpecieId src;

	protected SpecieEvent(SpecieId src) {
		super();
		this.src = src;
	}

	public SpecieId getSrc() {
		return src;
	}
}
