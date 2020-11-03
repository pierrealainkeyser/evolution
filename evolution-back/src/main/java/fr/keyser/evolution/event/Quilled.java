package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class Quilled extends SpecieEvent implements AttackEvent {

	private final UsedTrait trait;

	@JsonCreator
	public Quilled(@JsonProperty("src") SpecieId src,@JsonProperty("trait") UsedTrait trait) {
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
