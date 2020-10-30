package fr.keyser.evolution.event;

import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class PopulationGrow extends PopulationChanged {

	private final UsedTrait trait;

	public PopulationGrow(SpecieId src, int to, UsedTrait trait) {
		super(src, to);
		this.trait = trait;
	}

	public UsedTrait getTrait() {
		return trait;
	}

}
