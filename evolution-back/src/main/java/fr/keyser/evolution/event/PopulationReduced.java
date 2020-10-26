package fr.keyser.evolution.event;

import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class PopulationReduced extends PopulationChanged {

	private final UsedTrait trait;

	public PopulationReduced(SpecieId src, int to, UsedTrait trait) {
		super(src, to);
		this.trait = trait;
	}

	@Override
	public String toString() {
		return String.format("PopulationReduced [specie=%s, population=%s, trait=%s]", getSrc(), getTo(), trait);
	}

	public UsedTrait getTrait() {
		return trait;
	}
}
