package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class PopulationReduced extends PopulationChanged {

	private final UsedTrait trait;

	@JsonCreator
	public PopulationReduced(@JsonProperty("src") SpecieId src, @JsonProperty("to") int to,
			@JsonProperty("trait") UsedTrait trait) {
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
