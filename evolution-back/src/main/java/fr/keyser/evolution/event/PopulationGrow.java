package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class PopulationGrow extends PopulationChanged {

	private final UsedTrait trait;

	@JsonCreator
	public PopulationGrow(@JsonProperty("src") SpecieId src, @JsonProperty("to") int to,
			@JsonProperty("trait") UsedTrait trait) {
		super(src, to);
		this.trait = trait;
	}

	public UsedTrait getTrait() {
		return trait;
	}

	@Override
	public String toString() {
		return String.format("PopulationGrow [species=%s, population=%s, trait=%s]", getSrc(), getTo(), trait);
	}

}
