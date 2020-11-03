package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.UsedTrait;

public class DiscardPoolFood implements PoolEvent {

	private final int delta;

	private final int food;

	private final UsedTrait trait;

	@JsonCreator
	public DiscardPoolFood(@JsonProperty("delta") int delta, @JsonProperty("food") int food,
			@JsonProperty("trait") UsedTrait trait) {
		this.delta = delta;
		this.food = food;
		this.trait = trait;
	}

	public int getDelta() {
		return delta;
	}

	public int getFood() {
		return food;
	}

	@Override
	public String toString() {
		return String.format("DiscardPoolFood [delta=%s, food=%s, trait=%s]", delta, food, trait);
	}

	public UsedTrait getTrait() {
		return trait;
	}
}
