package fr.keyser.evolution.event;

import fr.keyser.evolution.model.SpecieId;

public abstract class PopulationChanged extends SpecieEvent {
	private final int to;

	protected PopulationChanged(SpecieId src, int to) {
		super(src);
		this.to = to;
	}

	public int getTo() {
		return to;
	}
}
