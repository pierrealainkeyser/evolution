package fr.keyser.evolution.event;

import fr.keyser.evolution.model.SpecieId;

public class FatMoved extends SpecieEvent {

	private final int fat;

	public FatMoved(SpecieId src, int fat) {
		super(src);
		this.fat = fat;
	}

	public int getFat() {
		return fat;
	}
}
