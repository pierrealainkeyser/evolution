package fr.keyser.evolution.event;

import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class Quilled extends SpecieEvent implements AttackEvent {

	private final UsedTrait trait;

	public Quilled(SpecieId src, UsedTrait trait) {
		super(src);
		this.trait = trait;
	}

	public UsedTrait getTrait() {
		return trait;
	}

	@Override
	public String toString() {
		return String.format("Quilled [specie=%s, trait=%s]", getSrc(), trait);
	}

}
