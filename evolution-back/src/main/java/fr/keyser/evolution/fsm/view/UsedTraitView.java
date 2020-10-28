package fr.keyser.evolution.fsm.view;

import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.model.UsedTrait;

public class UsedTraitView {

	private final UsedTrait trait;

	public UsedTraitView(UsedTrait trait) {
		this.trait = trait;
	}

	public String getSpecie() {
		return trait.getSpecie().toString();
	}

	public Trait getTrait() {
		return trait.getTrait();
	}
}
