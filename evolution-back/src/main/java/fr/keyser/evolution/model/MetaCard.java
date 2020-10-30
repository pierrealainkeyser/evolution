package fr.keyser.evolution.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetaCard {

	private final Trait trait;

	private final int food;

	public MetaCard(@JsonProperty("trait") Trait trait,
			@JsonProperty("food") int food) {
		this.trait = trait;
		this.food = food;
	}

	public Trait getTrait() {
		return trait;
	}

	public int getFood() {
		return food;
	}
}
